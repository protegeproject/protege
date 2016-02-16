package org.protege.editor.owl.model.search.nci;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.SearchContext;
import org.protege.editor.owl.model.search.SearchInterruptionException;
import org.protege.editor.owl.model.search.SearchResult;
import org.protege.editor.owl.model.search.SearchResultHandler;
import org.protege.editor.owl.model.search.SearchStringParser;
import org.protege.editor.owl.model.search.lucene.ChangeSet;
import org.protege.editor.owl.model.search.lucene.IndexDelegator;
import org.protege.editor.owl.model.search.lucene.LuceneSearcher;
import org.protege.editor.owl.model.search.lucene.QueryRunner;
import org.protege.editor.owl.model.search.lucene.ResultDocumentHandler;
import org.protege.editor.owl.model.search.lucene.SearchQueries;

import org.apache.lucene.search.IndexSearcher;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.util.ProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.SwingUtilities;

import com.google.common.base.Stopwatch;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 13/11/2015
 */
public class NciSearchManager extends LuceneSearcher {

    private static final Logger logger = LoggerFactory.getLogger(NciSearchManager.class);

    private OWLEditorKit editorKit;

    private Set<SearchCategory> categories = new HashSet<>();

    private ExecutorService service = Executors.newSingleThreadExecutor();

    private AtomicLong lastSearchId = new AtomicLong(0);

    private SearchStringParser searchStringParser = new NciSearchStringParser();

    private NciThesaurusIndexer indexer = new NciThesaurusIndexer();

    private IndexDelegator indexDelegator;
    private Map<OWLOntology, IndexDelegator> indexDelegatorCache = new HashMap<>();

    private IndexSearcher indexSearcher;

    private OWLOntologyChangeListener ontologyChangeListener;

    private OWLModelManagerListener modelManagerListener;

    private final List<ProgressMonitor> progressMonitors = new ArrayList<>();

    public NciSearchManager() {
        // NO-OP
    }

    @Override
    public void initialise() {
        this.editorKit = getEditorKit();
        categories.add(SearchCategory.DISPLAY_NAME);
        categories.add(SearchCategory.IRI);
        categories.add(SearchCategory.ANNOTATION_VALUE);
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

    private void handleModelManagerEvent(OWLModelManagerChangeEvent event) {
        if (isCacheMutatingEvent(event)) {
            markCacheAsStale();
        }
        else if (isCacheSavingEvent(event)) {
            indexer.save(indexDelegator);
        }
    }

    private void prepareIndexDelegator() {
        OWLOntology activeOntology = editorKit.getOWLModelManager().getActiveOntology();
        IndexDelegator indexDelegator = indexDelegatorCache.get(activeOntology);
        if (indexDelegator == null) {
            indexDelegator = new IndexDelegator(activeOntology);
            indexDelegatorCache.put(activeOntology, indexDelegator);
        }
        this.indexDelegator = indexDelegator;
    }

    private void markCacheAsStale() {
        lastSearchId.set(0); // rebuild index
    }

    private boolean isCacheMutatingEvent(OWLModelManagerChangeEvent event) {
        return event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(EventType.ENTITY_RENDERER_CHANGED) || event.isType(EventType.ENTITY_RENDERING_CHANGED);
    }

    private boolean isCacheSavingEvent(OWLModelManagerChangeEvent event) {
        return event.isType(EventType.ONTOLOGY_SAVED);
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
        OWLModelManager mm = editorKit.getOWLModelManager();
        disposeListeners(mm);
        closeIndexes(mm);
    }

    private void disposeListeners(OWLModelManager mm) {
        mm.removeOntologyChangeListener(ontologyChangeListener);
        mm.removeListener(modelManagerListener);
    }

    private void closeIndexes(OWLModelManager mm) {
        for (OWLOntology ontology : indexDelegatorCache.keySet()) {
            IndexDelegator indexDelegator = indexDelegatorCache.get(ontology);
            if (mm.isDirty(ontology)) {
                indexer.revert(indexDelegator);
            } else {
                indexer.close(indexDelegator);
            }
        }
    }

    @Override
    public IndexSearcher getIndexSearcher() {
        return indexSearcher;
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

    @Override
    public void performSearch(String searchString, SearchResultHandler searchResultHandler) {
        if (lastSearchId.getAndIncrement() == 0) {
            prepareIndexDelegator();
            service.submit(this::buildingIndex);
        }
        UserQueries query = prepareQuery(searchString);
        service.submit(new SearchCallable(lastSearchId.incrementAndGet(), query, searchResultHandler));
    }

    private void buildingIndex() {
        fireIndexingStarted();
        try {
            indexer.doIndex(indexDelegator,
                    new SearchContext(editorKit),
                    progress -> fireIndexingProgressed(progress));
            reloadIndexSearcher();
        }
        catch (IOException e) {
            logger.error("... build index failed");
            e.printStackTrace();
        }
        finally {
            fireIndexingFinished();
        }
    }

    private void updateIndex(List<? extends OWLOntologyChange> changes) {
        service.submit(() -> updatingIndex(changes));
    }

    private void updatingIndex(List<? extends OWLOntologyChange> changes) {
        try {
            boolean success = indexer.doUpdate(indexDelegator,
                    new ChangeSet(changes),
                    new SearchContext(editorKit));
            if (success) {
                reloadIndexSearcher();
            }
        }
        catch (IOException e) {
            logger.error("... update index failed");
            e.printStackTrace();
        }
    }

    private void reloadIndexSearcher() {
        indexSearcher = new IndexSearcher(indexer.getIndexReader(indexDelegator));
    }

    private UserQueries prepareQuery(String searchString) {
        NciQueryBasedInputHandler handler = new NciQueryBasedInputHandler(this);
        searchStringParser.parse(searchString, handler);
        return handler.getQueryObject();
    }

    private class SearchCallable implements Runnable {

        private long searchId;
        private UserQueries userQueries;
        private SearchResultHandler searchResultHandler;
        private QueryRunner queryRunner = new QueryRunner(lastSearchId);

        private SearchCallable(long searchId, UserQueries userQueries, SearchResultHandler searchResultHandler) {
            this.searchId = searchId;
            this.userQueries = userQueries;
            this.searchResultHandler = searchResultHandler;
        }

        @Override
        public void run() {
            logger.debug("Starting search " + searchId + "\n" + userQueries);
            SearchResultManager resultManager = new SearchResultManager();
            Stopwatch stopwatch = Stopwatch.createStarted();
            fireSearchStarted();
            try {
                for (Entry<SearchQueries, Boolean> queryEntry : userQueries) {
                    runQuery(queryEntry.getKey(), queryEntry.getValue(), resultManager);
                }
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
            Set<SearchResult> results = resultManager.getSearchResults();
            stopwatch.stop();
            logger.debug("... finished search {} in {} ms ({} results)", searchId, stopwatch.elapsed(TimeUnit.MILLISECONDS), results.size());
            showResults(results, searchResultHandler);
        }

        private void runQuery(SearchQueries searchQueries, boolean isLinked, final SearchResultManager resultManager) throws IOException, SearchInterruptionException {
            ResultDocumentHandler documentHandler = new ResultDocumentHandler(editorKit);
            queryRunner.execute(searchId, searchQueries, documentHandler, progress -> fireSearchProgressed(progress));
            Set<SearchResult> searchResults = documentHandler.getSearchResults();
            resultManager.addSearchResults(searchResults, isLinked);
        }

        private void showResults(final Set<SearchResult> results, final SearchResultHandler searchResultHandler) {
            if (SwingUtilities.isEventDispatchThread()) {
                searchResultHandler.searchFinished(results);
            }
            else {
                SwingUtilities.invokeLater(() -> searchResultHandler.searchFinished(results));
            }
        }
    }

    private void fireIndexingStarted() {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                pm.setSize(100);
                pm.setMessage("Initializing index");
                pm.setStarted();
            }
        });
    }

    private void fireIndexingProgressed(final long progress) {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                pm.setProgress(progress);
                switch ((int)progress % 4) {
                    case 0: pm.setMessage("indexing"); break;
                    case 1: pm.setMessage("indexing."); break;
                    case 2: pm.setMessage("indexing.."); break;
                    case 3: pm.setMessage("indexing..."); break;
                }
            }
        });
    }

    private void fireIndexingFinished() {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                pm.setFinished();
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

    private void fireSearchProgressed(final long progress) {
        SwingUtilities.invokeLater(() -> {
            for (ProgressMonitor pm : progressMonitors) {
                pm.setProgress(progress);
                switch ((int)progress % 4) {
                    case 0: pm.setMessage("searching"); break;
                    case 1: pm.setMessage("searching."); break;
                    case 2: pm.setMessage("searching.."); break;
                    case 3: pm.setMessage("searching..."); break;
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
