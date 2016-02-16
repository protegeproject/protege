package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.search.ResultItem;
import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchManager;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultHandler;
import org.protege.editor.owl.model.search.SearchResultMatch;
import org.protege.editor.owl.model.search.SearchSettings;
import org.protege.editor.owl.model.search.SearchStringParser;

import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/09/2012
 */
public class DefaultSearchManager extends SearchManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSearchManager.class);

    private OWLEditorKit editorKit;

    private Set<SearchCategory> categories = new HashSet<>();

    private ExecutorService service = Executors.newSingleThreadExecutor();

    private AtomicLong lastSearchId = new AtomicLong(0);

    private SearchSettings settings = new SearchSettings();

    private List<SearchMetadata> searchMetadataCache = new ArrayList<SearchMetadata>();

    private SearchStringParser searchStringParser = new SearchStringParserImpl();

    private OWLOntologyChangeListener ontologyChangeListener;

    private OWLModelManagerListener modelManagerListener;

    private SearchMetadataImportManager importManager;

    private final List<ProgressMonitor> progressMonitors = new ArrayList<>();

    public DefaultSearchManager() {
        // NO-OP
    }

    @Override
    public void initialise() {
        this.editorKit = getEditorKit();
        this.importManager = new SearchMetadataImportManager();
        categories.add(SearchCategory.DISPLAY_NAME);
        categories.add(SearchCategory.IRI);
        categories.add(SearchCategory.ANNOTATION_VALUE);
        categories.add(SearchCategory.LOGICAL_AXIOM);
        ontologyChangeListener = changes -> markCacheAsStale();
        modelManagerListener = this::handleModelManagerEvent;
        editorKit.getModelManager().addListener(modelManagerListener);
        editorKit.getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
    }

    @Override
    public void addProgressMonitor(ProgressMonitor pm) {
        progressMonitors.add(pm);
    }

    @Override
    public void dispose() {
        if (editorKit == null) {
            return;
        }
        OWLModelManager modelMan = editorKit.getOWLModelManager();
        modelMan.removeOntologyChangeListener(ontologyChangeListener);
        modelMan.removeListener(modelManagerListener);
    }

    public PatternBasedInputHandler getSearchInputHandler() {
        return new PatternBasedInputHandler();
    }

    private void handleModelManagerEvent(OWLModelManagerChangeEvent event) {
        if (isCacheMutatingEvent(event)) {
            markCacheAsStale();
        }
    }

    private boolean isCacheMutatingEvent(OWLModelManagerChangeEvent event) {
        return event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(EventType.ENTITY_RENDERER_CHANGED) || event.isType(EventType.ENTITY_RENDERING_CHANGED);
    }


    private void markCacheAsStale() {
        lastSearchId.set(0);
    }

    @Override
    public boolean isSearchType(SearchCategory category) {
        return categories.contains(category);
    }

    @Override
    public void setCategories(Collection<SearchCategory> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        markCacheAsStale();
    }

    private void rebuildMetadataCache() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        logger.info("Rebuilding search metadata cache...");
        fireIndexingStarted();
        try {
            searchMetadataCache.clear();
            List<SearchMetadataImporter> importerList = importManager.getImporters();
            for (SearchMetadataImporter importer : importerList) {
                SearchMetadataDB db = importer.getSearchMetadata(editorKit, settings);
                searchMetadataCache.addAll(db.getResults());
            }
            stopwatch.stop();
            logger.info("...rebuilt search metadata cache in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        finally {
            fireIndexingFinished();
        }
    }

    @Override
    public void performSearch(final String searchString, final SearchResultHandler searchResultHandler) {
        if (lastSearchId.getAndIncrement() == 0) {
            service.submit(this::rebuildMetadataCache);
        }
        List<Pattern> searchPattern = prepareSearchPattern(searchString);
        service.submit(new SearchCallable(lastSearchId.incrementAndGet(), searchPattern, searchResultHandler));
    }

    private List<Pattern> prepareSearchPattern(String searchString) {
        PatternBasedInputHandler handler = new PatternBasedInputHandler();
        searchStringParser.parse(searchString, handler);
        return handler.getQueryObject();
    }

    private class SearchCallable implements Runnable {

        private long searchId;

        private List<Pattern> searchPattern;

        private SearchResultHandler searchResultHandler;

        private SearchCallable(long searchId, List<Pattern> searchPattern, SearchResultHandler searchResultHandler) {
            this.searchId = searchId;
            this.searchPattern = searchPattern;
            this.searchResultHandler = searchResultHandler;
        }

        @Override
        public void run() {
            StringBuilder patternString = new StringBuilder();
            for(Iterator<Pattern> it = searchPattern.iterator(); it.hasNext(); ) {
                Pattern pattern = it.next();
                patternString.append(pattern.pattern());
                if (it.hasNext()) {
                    patternString.append("  AND  ");
                }
            }
            logger.debug("Starting search {} (pattern: {})", searchId, patternString);

            List<SearchResult> results = new ArrayList<>();
            Stopwatch stopwatch = Stopwatch.createStarted();
            fireSearchStarted();
            long count = 0;
            int total = searchMetadataCache.size();
            int percent = 0;
            for (SearchMetadata searchMetadata : searchMetadataCache) {
                if (!isLatestSearch()) {
                    // New search started
                    logger.info("... terminating search {} prematurely", searchId);
                    return;
                }
                String text = searchMetadata.getSearchString();
                boolean matchedAllPatterns = true;
                int startIndex = 0;
                ImmutableList.Builder<SearchResultMatch> matchesBuilder = ImmutableList.builder();
                for(Pattern pattern : searchPattern) {
                    if(startIndex >= text.length()) {
                        matchedAllPatterns = false;
                        break;
                    }
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        SearchResultMatch match = new SearchResultMatch(pattern.pattern(), matcher.start(), matcher.end()-matcher.start());
                        matchesBuilder.add(match);
                        startIndex = matcher.end() + 1;
                    }
                    else {
                        matchedAllPatterns = false;
                        break;
                    }
                }
                if (matchedAllPatterns) {
                    ResultItem resultItem = new SearchMetadataWrapper(searchMetadata);
                    results.add(new SearchResult(resultItem, matchesBuilder.build()));
                }
                count++;
                int nextPercent = (int) ((count * 100) / total);
                if (nextPercent != percent) {
                    percent = nextPercent;
                    fireSearchProgressed(percent, results.size());
                }
            }
            DefaultSearchManager.this.fireSearchFinished();
            stopwatch.stop();
            logger.debug("... finished search {} in {} ms ({} results)", searchId, stopwatch.elapsed(TimeUnit.MILLISECONDS), results.size());
            fireSearchFinished(results, searchResultHandler);
        }

        private boolean isLatestSearch() {
            return searchId == lastSearchId.get();
        }

        private void fireSearchFinished(final List<SearchResult> results, final SearchResultHandler searchResultHandler) {
            if (SwingUtilities.isEventDispatchThread()) {
                searchResultHandler.searchFinished(results);
            }
            else {
                SwingUtilities.invokeLater(() -> searchResultHandler.searchFinished(results));
            }
        }
    }

    private void fireIndexingFinished() {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                pm.setFinished();
                pm.setIndeterminate(false);
            }
        });
    }

    private void fireIndexingStarted() {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                pm.setIndeterminate(true);
                pm.setMessage("Searching");
                pm.setStarted();
            }
        });
    }

    private void fireSearchStarted() {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                pm.setSize(100);
                pm.setStarted();
            }
        });
    }

    private void fireSearchProgressed(final long progress, final int found) {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                if (found > 1 || found == 0) {
                    pm.setMessage(found + " results");
                }
                else {
                    pm.setMessage(found + " result");
                }
            }
        });
    }

    private void fireSearchFinished() {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                pm.setFinished();
            }
        });
    }
}
