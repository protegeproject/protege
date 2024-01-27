package org.protege.editor.owl.model.search;

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

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/09/2012
 */
public class DefaultSearchManager extends SearchManager {

    private final Logger logger = LoggerFactory.getLogger(DefaultSearchManager.class);

    private OWLEditorKit editorKit;

    private ExecutorService service = Executors.newSingleThreadExecutor();

    private AtomicLong lastSearchId = new AtomicLong(0);

    private Set<SearchCategory> categories = new HashSet<>();

    private List<SearchMetadata> searchMetadataCache = new ArrayList<>();

    private OWLOntologyChangeListener ontologyChangeListener;

    private OWLModelManagerListener modelManagerListener;

    private SearchMetadataImportManager importManager;

    private final List<ProgressMonitor> progressMonitors = new ArrayList<>();

    public DefaultSearchManager() {

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
        if(editorKit == null) {
            return;
        }
        OWLModelManager modelMan = editorKit.getOWLModelManager();
        modelMan.removeOntologyChangeListener(ontologyChangeListener);
        modelMan.removeListener(modelManagerListener);
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
                SearchMetadataDB db = importer.getSearchMetadata(editorKit, categories);
                searchMetadataCache.addAll(db.getResults());
            }
            stopwatch.stop();
            logger.info("    ...rebuilt search metadata cache in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
        catch (Exception e) {
            logger.error("An error occurred whilst rebuilding the search metadata cache: {}", e.getMessage(), e);
        }
        finally {
            fireIndexingFinished();
        }

    }


    @Override
    public void performSearch(final SearchRequest searchRequest, final SearchResultHandler searchResultHandler) {
        if (lastSearchId.getAndIncrement() == 0) {
            service.submit(this::rebuildMetadataCache);
        }
        service.submit(new SearchCallable(lastSearchId.incrementAndGet(), searchRequest, searchResultHandler));
    }


    private class SearchCallable implements Runnable {

        private long searchId;

        private SearchRequest searchRequest;

        private SearchResultHandler searchResultHandler;

        private SearchCallable(long searchId, SearchRequest searchRequest, SearchResultHandler searchResultHandler) {
            this.searchId = searchId;
            this.searchRequest = searchRequest;
            this.searchResultHandler = searchResultHandler;
        }

        public void run() {
            StringBuilder patternString = new StringBuilder();
            for(Iterator<Pattern> it = searchRequest.getSearchPatterns().iterator(); it.hasNext(); ) {
                Pattern pattern = it.next();
                patternString.append(pattern.pattern());
                if (it.hasNext()) {
                    patternString.append("  AND  ");
                }
            }
            logger.info("Starting search {} (pattern: {})", searchId, patternString);
            List<SearchResult> results = new ArrayList<>();


            long searchStartTime = System.currentTimeMillis();
            fireSearchStarted();
            long count = 0;
            int total = searchMetadataCache.size();
            int percent = 0;
            for (SearchMetadata searchMetadata : searchMetadataCache) {
                if (!isLatestSearch()) {
                    // New search started
                    logger.info("    Terminating search {} prematurely", searchId);
                    return;
                }
                String text = searchMetadata.getSearchString();
                boolean matchedAllPatterns = true;
                int startIndex = 0;
                ImmutableList.Builder<SearchResultMatch> matchesBuilder = ImmutableList.builder();
                for(Pattern pattern : searchRequest.getSearchPatterns()) {
                    if(startIndex >= text.length()) {
                        matchedAllPatterns = false;
                        break;
                    }
                    Matcher matcher = pattern.matcher(text);
                    if (matcher.find()) {
                        SearchResultMatch match = new SearchResultMatch(pattern, matcher.start(), matcher.end());
                        matchesBuilder.add(match);
                        startIndex = matcher.end() + 1;
                    }
                    else {
                        matchedAllPatterns = false;
                        break;
                    }
                }
                if (matchedAllPatterns) {

                    results.add(new SearchResult(searchMetadata, matchesBuilder.build()));
                }
                count++;
                int nextPercent = (int) ((count * 100) / total);
                if (nextPercent != percent) {
                    percent = nextPercent;
                    fireSearchProgressed(percent, results.size());
                }
            }
            DefaultSearchManager.this.fireSearchFinished();
            long searchEndTime = System.currentTimeMillis();
            long searchTime = searchEndTime - searchStartTime;
            logger.info("    Finished search {} in {} ms ({} results)", searchId, searchTime, results.size());
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
                SwingUtilities.invokeLater(() -> {
                    searchResultHandler.searchFinished(results);
                });
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
                pm.setProgress(progress);
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
