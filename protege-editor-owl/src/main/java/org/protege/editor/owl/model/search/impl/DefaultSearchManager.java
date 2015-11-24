package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.search.ResultItem;
import org.protege.editor.owl.model.search.SearchManager;
import org.protege.editor.owl.model.search.SearchRequest;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultHandler;
import org.protege.editor.owl.model.search.SearchResultMatch;
import org.protege.editor.owl.model.search.SearchSettings;
import org.protege.editor.owl.model.search.SearchSettingsListener;

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import com.google.common.collect.ImmutableList;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/09/2012
 */
public class DefaultSearchManager implements SearchManager, SearchSettingsListener {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSearchManager.class);

    private OWLEditorKit editorKit;

    private ExecutorService service = Executors.newSingleThreadExecutor();

    private AtomicLong lastSearchId = new AtomicLong(0);

    private SearchSettings settings = new SearchSettings();

    private List<SearchMetadata> searchMetadataCache = new ArrayList<SearchMetadata>();

    private ProgressMonitor progressMonitor;

    private Future<?> lastIndexingTask;
    private Future<?> lastSearchingTask;

    private final OWLOntologyChangeListener ontologyChangeListener;

    private final OWLModelManagerListener modelManagerListener;

    private final SearchMetadataImportManager importManager;

    public DefaultSearchManager(OWLEditorKit editorKit, SearchMetadataImportManager importManager) {
        this.editorKit = editorKit;
        this.importManager = importManager;
        ontologyChangeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                markCacheAsStale();
            }
        };
        modelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                handleModelManagerEvent(event);
            }
        };
        editorKit.getModelManager().addListener(modelManagerListener);
        editorKit.getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
    }

    @Override
    public void setProgressMonitor(ProgressMonitor pm) {
        progressMonitor = pm;
    }

    @Override
    public boolean canInterrupt() {
        return true;
    }

    @Override
    public void interrupt() throws InterruptedException {
        lastIndexingTask.cancel(true);
        lastSearchingTask.cancel(true);
    }

    @Override
    public void dispose() {
        OWLModelManager modelMan = editorKit.getOWLModelManager();
        modelMan.removeOntologyChangeListener(ontologyChangeListener);
        modelMan.removeListener(modelManagerListener);
    }

    @Override
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
    public void handleSearchSettingsChanged() {
        markCacheAsStale();
    }

    @Override
    public SearchSettings getSearchSettings() {
        return settings;
    }

    private void rebuildMetadataCache() {
        long t0 = System.currentTimeMillis();
        logger.info("Rebuilding search metadata cache...");
        fireIndexingStarted();
        try {
            searchMetadataCache.clear();
            List<SearchMetadataImporter> importerList = importManager.getImporters();
            for (SearchMetadataImporter importer : importerList) {
                SearchMetadataDB db = importer.getSearchMetadata(editorKit, settings);
                searchMetadataCache.addAll(db.getResults());
            }
            long t1 = System.currentTimeMillis();
            logger.info("    ...rebuilt search metadata cache in " + (t1 - t0) + " ms");
        }
        finally {
            fireIndexingFinished();
        }
    }

    @Override
    public void performSearch(final SearchRequest searchRequest, final SearchResultHandler searchResultHandler) {
        if (lastSearchId.getAndIncrement() == 0) {
            lastIndexingTask = service.submit(new Runnable() {
                public void run() {
                    rebuildMetadataCache();
                }
            });
         }
        lastSearchingTask = service.submit(new SearchCallable(lastSearchId.incrementAndGet(), searchRequest, searchResultHandler));
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

        @Override
        public void run() {
            StringBuilder patternString = new StringBuilder();
            @SuppressWarnings("unchecked")
            List<Pattern> searchPattern = (List<Pattern>) searchRequest.getSearchObject();
            for(Iterator<Pattern> it = searchPattern.iterator(); it.hasNext(); ) {
                Pattern pattern = it.next();
                patternString.append(pattern.pattern());
                if (it.hasNext()) {
                    patternString.append("  AND  ");
                }
            }
            logger.debug("Starting search " + searchId + " (pattern: " + patternString.toString() + ")");

            List<SearchResult> results = new ArrayList<SearchResult>();
            long searchStartTime = System.currentTimeMillis();
            fireSearchStarted();
            long count = 0;
            int total = searchMetadataCache.size();
            int percent = 0;
            for (SearchMetadata searchMetadata : searchMetadataCache) {
                if (!isLatestSearch()) {
                    // New search started
                    logger.debug("    terminating search " + searchId + " prematurely");
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
            long searchEndTime = System.currentTimeMillis();
            long searchTime = searchEndTime - searchStartTime;
            logger.debug("    finished search " + searchId + " in " + searchTime + " ms (" + results.size() + " results)");
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
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        searchResultHandler.searchFinished(results);
                    }
                });
            }
        }
    }

    private void fireIndexingFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressMonitor.setFinished();
                progressMonitor.setIndeterminate(false);
            }
        });
    }

    private void fireIndexingStarted() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressMonitor.setIndeterminate(true);
                progressMonitor.setMessage("Searching");
                progressMonitor.setStarted();
            }
        });
    }

    private void fireSearchStarted() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressMonitor.setSize(100);
                progressMonitor.setStarted();
            }
        });
    }

    private void fireSearchProgressed(final long progress, final int found) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressMonitor.setProgress(progress);
                if (found > 1 || found == 0) {
                    progressMonitor.setMessage(found + " results");
                }
                else {
                    progressMonitor.setMessage(found + " result");
                }
            }
        });
    }

    private void fireSearchFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressMonitor.setFinished();
            }
        });
    }
}
