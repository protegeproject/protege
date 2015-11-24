package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.search.SearchManager;
import org.protege.editor.owl.model.search.SearchRequest;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultHandler;
import org.protege.editor.owl.model.search.SearchSettings;
import org.protege.editor.owl.model.search.SearchSettingsListener;

import org.apache.lucene.search.Query;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.SwingUtilities;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public class LuceneSearchManager implements SearchManager, SearchSettingsListener {

    private static final Logger logger = LoggerFactory.getLogger(LuceneSearchManager.class);

    private OWLEditorKit editorKit;

    private ExecutorService service = Executors.newSingleThreadExecutor();

    private AtomicLong lastSearchId = new AtomicLong(0);

    private SearchSettings settings = new SearchSettings();

    private ProgressMonitor progressMonitor;

    private QueryRunner queryRunner;

    private Future<?> lastIndexingTask;
    private Future<?> lastSearchingTask;

    private final OWLOntologyChangeListener ontologyChangeListener;

    private final OWLModelManagerListener modelManagerListener;

    public LuceneSearchManager(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        ontologyChangeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                updateIndex();
            }
        };
        modelManagerListener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                handleModelManagerEvent(event);
            }
        };
        editorKit.getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
        editorKit.getModelManager().addListener(modelManagerListener);
    }

    @Override
    public void setProgressMonitor(ProgressMonitor pm) {
        progressMonitor = pm;
    }

    @Override
    public boolean canInterrupt() {
        return false;
    }

    @Override
    public void interrupt() throws InterruptedException {
        lastIndexingTask.cancel(true);
        lastSearchingTask.cancel(true);
    }

    @Override
    public void dispose() throws Exception {
        OWLModelManager mm = editorKit.getOWLModelManager();
        mm.removeOntologyChangeListener(ontologyChangeListener);
        mm.removeListener(modelManagerListener);
    }

    @Override
    public void handleSearchSettingsChanged() {
        // TODO Auto-generated method stub
    }

    @Override
    public SearchSettings getSearchSettings() {
        return settings;
    }

    @Override
    public QueryBasedInputHandler getSearchInputHandler() {
        return new QueryBasedInputHandler();
    }

    private void buildingIndex() {
        long t0 = System.currentTimeMillis();
        logger.info("Building search index...");
        fireIndexingStarted();
        try {
            AbstractLuceneIndexer indexer = new LuceneIndexer();
            indexer.doIndex(editorKit, progress -> fireIndexingProgressed(progress));
            indexer.close();
            long t1 = System.currentTimeMillis();
            logger.info("... built search index in " + (t1 - t0) + " ms");
            LuceneSearcher searcher = indexer.getSearcher();
            queryRunner = new QueryRunner(searcher);
        }
        catch (IOException e) {
            logger.error("... build index failed");
            e.printStackTrace();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        finally {
            fireIndexingFinished();
        }
    }

    private void updateIndex() {
        // TODO Auto-generated method stub
        
    }

    private void handleModelManagerEvent(OWLModelManagerChangeEvent event) {
        if (isCacheMutatingEvent(event)) {
            updateIndex();
        }
    }

    private boolean isCacheMutatingEvent(OWLModelManagerChangeEvent event) {
        return event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(EventType.ENTITY_RENDERER_CHANGED) || event.isType(EventType.ENTITY_RENDERING_CHANGED);
    }

    @Override
    public void performSearch(SearchRequest searchRequest, SearchResultHandler searchResultHandler) {
        if (lastSearchId.getAndIncrement() == 0) {
            lastIndexingTask = service.submit(new Runnable() {
                public void run() {
                    buildingIndex();
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
            BatchQuery batchQuery = (BatchQuery) searchRequest.getSearchObject();
            logger.debug("Starting search " + searchId + " (pattern: " + batchQuery + ")");
            
            Set<SearchResult> results = new HashSet<>();
            long searchStartTime = System.currentTimeMillis();
            fireSearchStarted();
            try {
                ResultDocumentHandler documentHandler = new ResultDocumentHandler(editorKit);
                for (Query query : batchQuery) {
                    if (!isLatestSearch()) return; // search got interrupted
                    if (query instanceof SearchQuery) {
                        SearchQuery searchQuery = (SearchQuery) query;
                        queryRunner.execute(searchQuery, documentHandler, progress -> fireSearchProgressed(progress));
                        results.addAll(documentHandler.getSearchResults());
                    }
                }
            }
            catch (Throwable e) {
                logger.error("... error while searching: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            fireSearchFinished();
            long searchEndTime = System.currentTimeMillis();
            long searchTime = searchEndTime - searchStartTime;
            logger.debug("... finished search " + searchId + " in " + searchTime + " ms (" + results.size() + " results)");
            showResults(results, searchResultHandler);
        }

        private boolean isLatestSearch() {
            return searchId == lastSearchId.get();
        }

        private void showResults(final Set<SearchResult> results, final SearchResultHandler searchResultHandler) {
            if (SwingUtilities.isEventDispatchThread()) {
                searchResultHandler.searchFinished(results);
            }
            else {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        searchResultHandler.searchFinished(results);
                    }
                });
            }
        }
    }

    private void fireIndexingStarted() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressMonitor.setSize(100);
                progressMonitor.setMessage("Initializing index");
                progressMonitor.setStarted();
            }
        });
    }

    private void fireIndexingProgressed(final long progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressMonitor.setProgress(progress);
                switch ((int)progress % 4) {
                    case 0: progressMonitor.setMessage("indexing"); break;
                    case 1: progressMonitor.setMessage("indexing."); break;
                    case 2: progressMonitor.setMessage("indexing.."); break;
                    case 3: progressMonitor.setMessage("indexing..."); break;
                }
            }
        });
    }

    private void fireIndexingFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressMonitor.setFinished();
            }
        });
    }

    private void fireSearchStarted() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressMonitor.setSize(100);
                progressMonitor.setStarted();
            }
        });
    }

    private void fireSearchProgressed(final long progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressMonitor.setProgress(progress);
                switch ((int)progress % 4) {
                    case 0: progressMonitor.setMessage("searching"); break;
                    case 1: progressMonitor.setMessage("searching."); break;
                    case 2: progressMonitor.setMessage("searching.."); break;
                    case 3: progressMonitor.setMessage("searching..."); break;
                }
            }
        });
    }

    private void fireSearchFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressMonitor.setFinished();
            }
        });
    }
}
