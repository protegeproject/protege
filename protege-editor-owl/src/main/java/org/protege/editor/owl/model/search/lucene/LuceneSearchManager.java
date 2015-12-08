package org.protege.editor.owl.model.search.lucene;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.search.SearchInterruptionException;
import org.protege.editor.owl.model.search.SearchManager;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultHandler;
import org.protege.editor.owl.model.search.SearchSettings;
import org.protege.editor.owl.model.search.SearchSettingsListener;
import org.protege.editor.owl.model.search.SearchStringParser;

import org.apache.lucene.search.IndexSearcher;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
public class LuceneSearchManager extends LuceneSearcher implements SearchManager, SearchSettingsListener {

    private static final Logger logger = LoggerFactory.getLogger(LuceneSearchManager.class);

    private OWLEditorKit editorKit;

    private ExecutorService service = Executors.newSingleThreadExecutor();

    private AtomicLong lastSearchId = new AtomicLong(0);

    private SearchSettings settings = new SearchSettings();

    private SearchStringParser searchStringParser = new KeywordStringParser();

    private ProgressMonitor progressMonitor;

    private LuceneIndexer indexer = new LuceneIndexer();
    
    private IndexSearcher indexSearcher;

    private Future<?> lastIndexingTask;
    private Future<?> lastSearchingTask;

    private final OWLOntologyChangeListener ontologyChangeListener;

    private final OWLModelManagerListener modelManagerListener;

    public LuceneSearchManager(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        ontologyChangeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                updateIndex(changes);
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
    public IndexSearcher getIndexSearcher() {
        return indexSearcher;
    }

    @Override
    public SearchSettings getSearchSettings() {
        return settings;
    }

    private void buildingIndex() {
        long t0 = System.currentTimeMillis();
        logger.info("Building search index...");
        fireIndexingStarted();
        try {
            indexer.start();
            indexer.doIndex(editorKit, progress -> fireIndexingProgressed(progress));
            indexer.close();
            long t1 = System.currentTimeMillis();
            logger.info("... built search index in " + (t1 - t0) + " ms");
            indexSearcher = new IndexSearcher(indexer.getIndexReader());
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

    private void updateIndex(List<? extends OWLOntologyChange> changes) {
        lastIndexingTask = service.submit(new Runnable() {
            public void run() {
                updatingIndex(changes);
            }
        });
    }

    private void updatingIndex(List<? extends OWLOntologyChange> changes) {
        long t0 = System.currentTimeMillis();
        logger.info("Updating search index...");
        try {
            indexer.restart();
            for (OWLOntologyChange change : changes) {
                OWLAxiom changedAxiom = change.getAxiom();
                OWLOntology sourceOntology = change.getOntology();
                if (change instanceof RemoveAxiom) {
                    if (changedAxiom instanceof OWLAnnotationAssertionAxiom) {
                        OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) changedAxiom).getSubject();
                        if (subject instanceof IRI) {
                            Optional<OWLEntity> entity = sourceOntology.getEntitiesInSignature((IRI) subject).stream().findFirst();
                            if (entity.isPresent()) {
                                indexer.doDelete(entity.get());
                            }
                        }
                    } else if (changedAxiom instanceof OWLDeclarationAxiom) {
                        OWLEntity entity = ((OWLDeclarationAxiom) changedAxiom).getEntity();
                        indexer.doDelete(entity);
                    }
                } else if (change instanceof AddAxiom) {
                    if (changedAxiom instanceof OWLAnnotationAssertionAxiom) {
                        OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiom) changedAxiom).getSubject();
                        if (subject instanceof IRI) {
                            Optional<OWLEntity> entity = sourceOntology.getEntitiesInSignature((IRI) subject).stream().findFirst();
                            if (entity.isPresent()) {
                                indexer.doAdd(editorKit, sourceOntology, entity.get());
                            }
                        }
                    } else if (changedAxiom instanceof OWLDeclarationAxiom) {
                        OWLEntity entity = ((OWLDeclarationAxiom) changedAxiom).getEntity();
                        indexer.doAdd(editorKit, sourceOntology, entity);
                    }
                }
            }
            indexer.close();
            long t1 = System.currentTimeMillis();
            logger.info("... updated search index in " + (t1 - t0) + " ms");
            indexSearcher = new IndexSearcher(indexer.getIndexReader());
        }
        catch (IOException e) {
            logger.error("... update index failed");
            e.printStackTrace();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void handleModelManagerEvent(OWLModelManagerChangeEvent event) {
        if (isCacheMutatingEvent(event)) {
            lastSearchId.set(0); // rebuild index
        }
    }

    private boolean isCacheMutatingEvent(OWLModelManagerChangeEvent event) {
        return event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(EventType.ENTITY_RENDERER_CHANGED);
    }

    @Override
    public void performSearch(String searchString, SearchResultHandler searchResultHandler) {
        if (lastSearchId.getAndIncrement() == 0) {
            lastIndexingTask = service.submit(new Runnable() {
                public void run() {
                    buildingIndex();
                }
            });
        }
        SearchQueries searchQueries = prepareQuery(searchString);
        lastSearchingTask = service.submit(new SearchCallable(lastSearchId.incrementAndGet(), searchQueries, searchResultHandler));
    }

    public SearchQueries prepareQuery(String searchString) {
        QueryBasedInputHandler handler = new QueryBasedInputHandler(this);
        searchStringParser.parse(searchString, handler);
        return handler.getSearchQuery();
    }

    private class SearchCallable implements Runnable {

        private long searchId;
        private SearchQueries searchQueries;
        private SearchResultHandler searchResultHandler;
        private QueryRunner queryRunner = new QueryRunner(lastSearchId);

        private SearchCallable(long searchId, SearchQueries searchQueries, SearchResultHandler searchResultHandler) {
            this.searchId = searchId;
            this.searchQueries = searchQueries;
            this.searchResultHandler = searchResultHandler;
        }

        @Override
        public void run() {
            logger.debug("Starting search " + searchId + " (pattern: " + searchQueries + ")");
            long searchStartTime = System.currentTimeMillis();
            fireSearchStarted();
            ResultDocumentHandler documentHandler = new ResultDocumentHandler(editorKit);
            try {
                queryRunner.execute(searchId, searchQueries, documentHandler, progress -> fireSearchProgressed(progress));
            }
            catch (SearchInterruptionException e) {
                return; // search terminated prematurely
            }
            catch (Throwable e) {
                logger.error("... error while searching: " + e.getMessage());
                e.printStackTrace();
                return;
            }
            fireSearchFinished();
            Set<SearchResult> results = documentHandler.getSearchResults();
            long searchEndTime = System.currentTimeMillis();
            long searchTime = searchEndTime - searchStartTime;
            logger.debug("... finished search " + searchId + " in " + searchTime + " ms (" + results.size() + " results)");
            showResults(results, searchResultHandler);
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
