package org.protege.editor.owl.model;

import com.google.common.base.Stopwatch;
import org.protege.editor.core.AbstractModelManager;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCache;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCacheImpl;
import org.protege.editor.owl.model.cache.OWLObjectRenderingCache;
import org.protege.editor.owl.model.classexpression.anonymouscls.AnonymousDefinedClassManager;
import org.protege.editor.owl.model.entity.CustomOWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.model.find.OWLEntityFinderImpl;
import org.protege.editor.owl.model.hierarchy.OWLHierarchyManager;
import org.protege.editor.owl.model.hierarchy.OWLHierarchyManagerImpl;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.model.history.HistoryManagerImpl;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.OWLReasonerManagerImpl;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.protege.editor.owl.model.io.*;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.model.selection.ontologies.ImportsClosureOntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;
import org.protege.editor.owl.model.util.ListenerManager;
import org.protege.editor.owl.ui.OWLObjectComparator;
import org.protege.editor.owl.ui.OWLObjectRenderingComparator;
import org.protege.editor.owl.ui.clsdescriptioneditor.ManchesterOWLExpressionCheckerFactory;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionCheckerFactory;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandler;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.renderer.*;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;
import org.protege.xmlcatalog.XMLCatalog;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The <code>OWLModelManager</code> acts as a controller
 * over a collection of ontologies (ontologies that are
 * related to each other via owl:imports) and the various
 * UI components that are used to access the ontology.
 */
public class OWLModelManagerImpl extends AbstractModelManager implements OWLModelManager, OWLEntityRendererListener, OWLOntologyChangeListener, OWLOntologyLoaderListener, IOListenerManager {

    private final Logger logger = LoggerFactory.getLogger(OWLModelManagerImpl.class);

    private final HistoryManager historyManager;

    private OWLModelManagerEntityRenderer entityRenderer;

    private OWLObjectRenderer objectRenderer;

    private OWLOntology activeOntology;

    private OWLEntityRenderingCache owlEntityRenderingCache;

    /**
     * P4 repeatedly asks for the same rendering multiple times in a row
     * because of the components listening to mouse events etc so cache a
     * small number of objects we have just rendered
     */
    private OWLObjectRenderingCache owlObjectRenderingCache;

    private OWLEntityFinder entityFinder;

    private final OWLReasonerManager owlReasonerManager;

    /**
     * Dirty ontologies are ontologies that have been edited
     * and not saved.
     */
    private final Set<OWLOntologyID> dirtyOntologies = new HashSet<>();

    /**
     * The <code>OWLConnection</code> that we use to manage
     * ontologies.
     */
    private final OWLOntologyManager manager;

    private final OntologyCatalogManager ontologyCatalogManager = new OntologyCatalogManager();

    private ExplanationManager explanationManager;

    private OWLEntityFactory entityFactory;

    /**
     * A cache for the imports closure.  Originally, we just requested this
     * each time from the OWLOntologyManager, but this proved to be expensive
     * in terms of time.
     */
    private final Set<OWLOntology> activeOntologies = new HashSet<>();

    private final Set<OntologySelectionStrategy> ontSelectionStrategies = new HashSet<>();

    private OntologySelectionStrategy activeOntologiesStrategy;

    private OWLExpressionCheckerFactory owlExpressionCheckerFactory;


    // error handlers

//    private SaveErrorHandler saveErrorHandler;

    private OntologyLoadErrorHandler loadErrorHandler;

    private final UserResolvedIRIMapper userResolvedIRIMapper = new UserResolvedIRIMapper(new MissingImportHandlerImpl());


    // listeners

    private final List<OWLModelManagerListener> modelManagerChangeListeners = new ArrayList<>();

    private final ListenerManager<OWLModelManagerListener> modelManagerListenerManager = new ListenerManager<>();

    private final ListenerManager<OWLOntologyChangeListener> changeListenerManager = new ListenerManager<>();

    private final List<IOListener> ioListeners = new ArrayList<>();


    public OWLModelManagerImpl() {
        super();

        manager = OWLManager.createConcurrentOWLOntologyManager();
        manager.addOntologyChangeListener(this);
        manager.addOntologyLoaderListener(this);

        // URI mappers for loading - added in reverse order
        PriorityCollection<OWLOntologyIRIMapper> iriMappers = manager.getIRIMappers();
        iriMappers.clear();
        iriMappers.add(userResolvedIRIMapper);
        iriMappers.add(new WebConnectionIRIMapper());
        AutoMappedRepositoryIRIMapper autoMappedRepositoryIRIMapper = new AutoMappedRepositoryIRIMapper(ontologyCatalogManager);
        iriMappers.add(autoMappedRepositoryIRIMapper);


        objectRenderer = new OWLObjectRendererImpl(this);
        owlEntityRenderingCache = new OWLEntityRenderingCacheImpl();
        owlEntityRenderingCache.setOWLModelManager(this);
        owlObjectRenderingCache = new OWLObjectRenderingCache(this);

        owlExpressionCheckerFactory = new ManchesterOWLExpressionCheckerFactory(this);

        //needs to be initialized
        activeOntologiesStrategy = new ImportsClosureOntologySelectionStrategy(this);

        historyManager = new HistoryManagerImpl(this);

        owlReasonerManager = new OWLReasonerManagerImpl(this);
        owlReasonerManager.getReasonerPreferences().addListener(() -> fireEvent(EventType.ONTOLOGY_CLASSIFIED));


        // force the renderer to be created
        // to prevent double cache rebuild once ontologies loaded
        getOWLEntityRenderer();

        put(OntologySourcesManager.ID, new OntologySourcesManager(this));


    }


    public void dispose() {
        super.dispose();

        OntologySourcesManager sourcesMngr = get(OntologySourcesManager.ID);
        removeIOListener(sourcesMngr);
        try {
            // Empty caches
            owlEntityRenderingCache.dispose();
            owlObjectRenderingCache.dispose();
            if (entityRenderer != null) {
                entityRenderer.dispose();
            }
            owlReasonerManager.dispose();
        } catch (Exception e) {
            logger.error("An error occurred whilst disposing of the model manager: {}", e.getMessage(), e);
        }

        // Name and shame plugins that do not (or can't be bothered to) clean up
        // their listeners!
        modelManagerListenerManager.dumpWarningForAllListeners(logger, "(Listeners should be removed in the plugin dispose method!)");

        changeListenerManager.dumpWarningForAllListeners(logger, "(Listeners should be removed in the plugin dispose method!)");
    }


    public boolean isDirty() {
        return !dirtyOntologies.isEmpty();
    }

    public boolean isDirty(OWLOntology ontology) {
        return dirtyOntologies.contains(ontology.getOntologyID());
    }

    public void setClean(OWLOntology ontology) {
        dirtyOntologies.remove(ontology.getOntologyID());
    }


    public OWLOntologyManager getOWLOntologyManager() {
        return manager;
    }


    public OntologyCatalogManager getOntologyCatalogManager() {
        return ontologyCatalogManager;
    }


    public OWLHierarchyManager getOWLHierarchyManager() {
        OWLHierarchyManager hm = get(OWLHierarchyManager.ID);
        if (hm == null) {
            hm = new OWLHierarchyManagerImpl(this);
            put(OWLHierarchyManager.ID, hm);
        }
        return hm;
    }

    public ExplanationManager getExplanationManager() {
        return explanationManager;
    }

    public void setExplanationManager(ExplanationManager explanationManager) {
        this.explanationManager = explanationManager;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //
    // Loading
    //
    ///////////////////////////////////////////////////////////////////////////////////////


    /**
     * A convenience method that loads an ontology from a file
     * The location of the file is specified by the URI argument.
     */
    public boolean loadOntologyFromPhysicalURI(URI uri) {
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        try {
            logger.info(LogBanner.start("Loading Ontology"));
            logger.info("Loading ontology from {}", uri);
            stopwatch.start();
            if (UIUtil.isLocalFile(uri)) {
                // Load the URIs of other ontologies that are contained in the same folder.
                File parentFile = new File(uri).getParentFile();
                addRootFolder(parentFile);
            }
            OntologyLoader loader = new OntologyLoader(this, userResolvedIRIMapper);
            Optional<OWLOntology> loadedOntology = loader.loadOntology(uri);
            return loadedOntology.isPresent();
        } catch (OWLOntologyCreationException e) {
            OWLOntologyID id = new OWLOntologyID(com.google.common.base.Optional.of(IRI.create(uri)), com.google.common.base.Optional.<IRI>absent());
            handleLoadError(id, uri, e);
            return false;
        } finally {
            logger.info("Loading for ontology and imports closure successfully completed in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            logger.info(LogBanner.end());
        }
    }


    public void startedLoadingOntology(LoadingStartedEvent event) {
        logger.info("Loading {} from {}", event.getOntologyID(), event.getDocumentIRI());
        fireBeforeLoadEvent(event.getOntologyID(), event.getDocumentIRI().toURI());
    }


    public void finishedLoadingOntology(LoadingFinishedEvent event) {
        if (!event.isSuccessful()) {
            Exception e = event.getException();
            handleLoadError(event.getOntologyID(), event.getDocumentIRI().toURI(), event.getException());
        }
        fireAfterLoadEvent(event.getOntologyID(), event.getDocumentIRI().toURI());
    }

    private void handleLoadError(OWLOntologyID owlOntologyID, URI documentURI, Exception e) {
        if (loadErrorHandler != null) {
            try {
                loadErrorHandler.handleErrorLoadingOntology(owlOntologyID, documentURI, e);
            } catch (Throwable e1) {
                // if, for any reason, the loadErrorHandler cannot report the error
                ErrorLogPanel.showErrorDialog(e1);
            }
        }
    }

    public XMLCatalog addRootFolder(File dir) {
        return ontologyCatalogManager.addFolder(dir);
    }

    @Override
    public void fireBeforeLoadEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for (IOListener listener : new ArrayList<>(ioListeners)) {
            try {
                listener.beforeLoad(new IOListenerEvent(ontologyID, physicalURI));
            } catch (Throwable e) {
                logger.warn("An IOListener threw an exception during event dispatch: {}", e);
            }
        }
    }


    @Override
    public void fireAfterLoadEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for (IOListener listener : new ArrayList<>(ioListeners)) {
            try {
                listener.afterLoad(new IOListenerEvent(ontologyID, physicalURI));
            } catch (Throwable e) {
                logger.warn("An IOListener threw an exception during event dispatch: {}", e);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Ontology URI to Physical URI mapping
    //
    ////////////////////////////////////////////////////////////////////////////////////////


    public URI getOntologyPhysicalURI(OWLOntology ontology) {
        IRI ontologyDocumentIRI = manager.getOntologyDocumentIRI(ontology);
        if (isDefaultOWLAPIDocumentIRI(ontologyDocumentIRI)) {
            return URI.create("");
        }
        else {
            return ontologyDocumentIRI.toURI();
        }
    }

    private boolean isDefaultOWLAPIDocumentIRI(IRI iri) {
        URI uri = iri.toURI();
        String scheme = uri.getScheme();
        return scheme != null && scheme.equals("owlapi");
    }

    public void setPhysicalURI(OWLOntology ontology, URI physicalURI) {
        manager.setOntologyDocumentIRI(ontology, IRI.create(physicalURI));
    }


    public OWLOntology createNewOntology(OWLOntologyID ontologyID, URI physicalURI) throws OWLOntologyCreationException {
        if (physicalURI != null && ontologyID.getDefaultDocumentIRI().isPresent()) {
            manager.getIRIMappers().add(new SimpleIRIMapper(ontologyID.getDefaultDocumentIRI().get(), IRI.create(physicalURI)));
        }
        OWLOntology ont = manager.createOntology(ontologyID);
        setActiveOntology(ont);
        if (physicalURI != null) {
            try {
                File containingDirectory = new File(physicalURI).getParentFile();
                if (containingDirectory.exists()) {
                    getOntologyCatalogManager().addFolder(containingDirectory);
                }
            } catch (IllegalArgumentException iae) {
                logger.warn("Cannot generate ontology catalog for ontology at " + physicalURI);
            }
        }
        fireEvent(EventType.ONTOLOGY_CREATED);
        return ont;
    }

    @Override
    public OWLOntology reload(OWLOntology ont) throws OWLOntologyCreationException {
        try {
            OntologyReloader reloader = new OntologyReloader(ont, this);
            reloader.reload();
            // Rebuild the cache in case the imports closure has changed
            rebuildActiveOntologiesCache();
            refreshRenderer();
        } finally {
            setClean(ont);
            fireEvent(EventType.ONTOLOGY_RELOADED);
        }
        return ont;
    }

    public boolean removeOntology(OWLOntology ont) {
        if (ont.equals(activeOntology)) {
            logger.debug("Request received to remove an ontology that is the active ontology.  Cannot remove the active ontology.  Ignoring request.");
            return false;
        }
        if (manager.contains(ont.getOntologyID()) && manager.getOntologies().size() == 1) {
            logger.debug("Request received to remove the one and only ontology that is open.  This is not allowed.  Ignoring request.");
            return false;
        }
        activeOntologies.remove(ont);
        dirtyOntologies.remove(ont.getOntologyID());
        manager.removeOntology(ont);
        setActiveOntology(activeOntology, true);

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Saving
    //
    ///////////////////////////////////////////////////////////////////////////////////////


    /**
     * Save all of the ontologies that are editable and that have been modified.
     * <p/>
     * This method should not be used as the behaviour is not clear.  The save(OWLOntology) method should be used
     * instead.
     */
    @Deprecated
    public void save() throws OWLOntologyStorageException {
        HashSet<OWLOntology> ontologiesToSave = new HashSet<>();
        for (OWLOntologyID ontId : dirtyOntologies) {
            if (manager.contains(ontId)) {
                ontologiesToSave.add(manager.getOntology(ontId));
            }
            else {
                dirtyOntologies.remove(ontId);
            }
        }
        for (OWLOntology ontology : ontologiesToSave) {
            save(ontology);
        }

    }


    public void save(OWLOntology ont) throws OWLOntologyStorageException {
        final URI documentURI = manager.getOntologyDocumentIRI(ont).toURI();

        fireBeforeSaveEvent(ont.getOntologyID(), documentURI);


        final OWLDocumentFormat format;
        final OWLDocumentFormat previousFormat = manager.getOntologyFormat(ont);
        if (previousFormat == null) {
            format = new RDFXMLDocumentFormat();
            logger.info("No document format for {} has been found.  " +
                            "Using the {} format.",
                    ont.getOntologyID(), format);
        }
        else {
            format = previousFormat;
        }
                /*
                 * Using the addMissingTypes call here for RDF/XML files can result in OWL Full output
                 * and can also result in data corruption.
                 *
                 * See http://protegewiki.stanford.edu/wiki/OWL2RDFParserDeclarationRequirement
                 */
        IRI documentIRI = IRI.create(documentURI);
        OntologySaver saver = OntologySaver.builder()
                .addOntology(ont, format, documentIRI)
                .build();
        saver.saveOntologies();
        ensureCatalogFileExists();
        manager.setOntologyDocumentIRI(ont, documentIRI);
        logger.info("Saved ontology {} to {} in {} format", ont.getOntologyID(), documentIRI, format);

        dirtyOntologies.remove(ont.getOntologyID());

        fireEvent(EventType.ONTOLOGY_SAVED);
        fireAfterSaveEvent(ont.getOntologyID(), documentURI);

    }

    private void ensureCatalogFileExists() {
        try {
            OWLOntology activeOntology = getActiveOntology();
            IRI ontologyDocumentIRI = manager.getOntologyDocumentIRI(activeOntology);
            if ("file".equals(ontologyDocumentIRI.getScheme())) {
                File file = new File(ontologyDocumentIRI.toURI());
                File parentDirectory = file.getParentFile();
                getOntologyCatalogManager().ensureCatalogExists(parentDirectory);
            }
        } catch (IllegalArgumentException e) {
            logger.error("A problem occurred when trying to save the catalog file: {}", e.getMessage());
        }
    }

    @Override
    public void fireBeforeSaveEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for (IOListener listener : new ArrayList<>(ioListeners)) {
            try {
                listener.beforeSave(new IOListenerEvent(ontologyID, physicalURI));
            } catch (Throwable e) {
                logger.warn("An IOListener threw an error during event dispatch: {}", e);
            }
        }
    }


    @Override
    public void fireAfterSaveEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for (IOListener listener : new ArrayList<>(ioListeners)) {
            try {
                listener.afterSave(new IOListenerEvent(ontologyID, physicalURI));
            } catch (Throwable e) {
                logger.warn("An IOListener threw an error during event dispatch: {}", e);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Ontology Management
    //
    ///////////////////////////////////////////////////////////////////////////////////////


    public Set<OWLOntology> getOntologies() {
        return manager.getOntologies();
    }


    public Set<OWLOntology> getDirtyOntologies() {
        Set<OWLOntology> ontologies = new HashSet<>();
        for (OWLOntologyID ontId : new ArrayList<>(dirtyOntologies)) {
            if (manager.contains(ontId)) {
                ontologies.add(manager.getOntology(ontId));
            }
            else {
                dirtyOntologies.remove(ontId);
            }
        }
        return ontologies;
    }


    /**
     * Forces the system to believe that an ontology
     * has been modified.
     *
     * @param ontology The ontology to be made dirty.
     */
    public void setDirty(OWLOntology ontology) {
        dirtyOntologies.add(ontology.getOntologyID());
    }


    public OWLOntology getActiveOntology() {
        return activeOntology;
    }


    public OWLDataFactory getOWLDataFactory() {
        return manager.getOWLDataFactory();
    }


    public Set<OWLOntology> getActiveOntologies() {
        return activeOntologies;
    }


    public boolean isActiveOntologyMutable() {
        return isMutable(getActiveOntology());
    }


    public boolean isMutable(OWLOntology ontology) {
        // Assume all ontologies are editable - even ones
        // that have been loaded from non-editable locations e.g.
        // the web.  The reason for this is that feedback from users
        // has indicated that it is a pain when an ontology isn't editable
        // just because it has been downloaded from a web because
        // they can't experiment with adding or removing axioms.
        return true;
    }


    public void setActiveOntology(OWLOntology activeOntology) {
        setActiveOntology(activeOntology, false);
    }


    public void setActiveOntologiesStrategy(OntologySelectionStrategy strategy) {
        activeOntologiesStrategy = strategy;
        setActiveOntology(getActiveOntology(), true);
        fireEvent(EventType.ONTOLOGY_VISIBILITY_CHANGED);
    }


    public OntologySelectionStrategy getActiveOntologiesStrategy() {
        return activeOntologiesStrategy;
    }


    public Set<OntologySelectionStrategy> getActiveOntologiesStrategies() {
        return ontSelectionStrategies;
    }


    /**
     * Sets the active ontology (and hence the set of active ontologies).
     *
     * @param activeOntology The ontology to be set as the active ontology.
     * @param force          By default, if the specified ontology is already the
     *                       active ontology then no changes will take place.  This flag can be
     *                       used to force the active ontology to be reset and listeners notified
     *                       of a change in the state of the active ontology.
     */
    private void setActiveOntology(OWLOntology activeOntology, boolean force) {
        if (!force) {
            if (this.activeOntology != null) {
                if (this.activeOntology.equals(activeOntology)) {
                    return;
                }
            }
        }
        this.activeOntology = activeOntology;
        logger.debug("Setting active ontology to " + activeOntology.getOntologyID());
        rebuildActiveOntologiesCache();
        // Rebuild entity indices
        entityRenderer.ontologiesChanged();
        rebuildEntityIndices();
        // Inform our listeners
        fireEvent(EventType.ACTIVE_ONTOLOGY_CHANGED);
    }


    public void registerOntologySelectionStrategy(OntologySelectionStrategy strategy) {
        ontSelectionStrategies.add(strategy);
    }


    private void rebuildActiveOntologiesCache() {
        activeOntologies.clear();
        activeOntologies.addAll(activeOntologiesStrategy.getOntologies());
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Ontology history management
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////


    public void applyChange(OWLOntologyChange change) {
        try {
            AnonymousDefinedClassManager adcManager = get(AnonymousDefinedClassManager.ID);
            if (adcManager != null) {
                change = adcManager.getChangeRewriter().rewriteChange(change);
            }
            applyChanges(Arrays.asList(change));
        } catch (OWLOntologyChangeException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public void applyChanges(List<? extends OWLOntologyChange> changes) {
        try {
            AnonymousDefinedClassManager adcManager = get(AnonymousDefinedClassManager.ID);
            if (adcManager != null) {
                changes = adcManager.getChangeRewriter().rewriteChanges(changes);
            }
            logger.info(LogBanner.start("Applying changes"));
            logger.info("Number of requested changes: {}", changes.size());
            List<OWLOntologyChange> minimizedChanges = new ChangeListMinimizer().getMinimisedChanges(changes);
            logger.info("Number of minimized changes: {}", minimizedChanges.size());
            logger.info(LogBanner.end());
            if (minimizedChanges.isEmpty()) {
                return;
            }
            manager.applyChanges(minimizedChanges);
        } catch (OWLOntologyChangeException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
        if (changes.isEmpty()) {
            return;
        }
        getHistoryManager().logChanges(changes);
        boolean refreshActiveOntology = false;
        for (OWLOntologyChange change : changes) {
            if (change instanceof SetOntologyID) {
                SetOntologyID ontologyIDChange = (SetOntologyID) change;
                dirtyOntologies.remove(ontologyIDChange.getOriginalOntologyID());
            }
            dirtyOntologies.add(change.getOntology().getOntologyID());
            if (change.isImportChange()) {
                refreshActiveOntology = true;
            }
        }
        if (refreshActiveOntology) {
            setActiveOntology(getActiveOntology(), true);
        }
    }


    public boolean isChangedEntity(OWLEntity entity) {
        return false;
    }


    public HistoryManager getHistoryManager() {
        return historyManager;
    }


    public void addOntologyChangeListener(OWLOntologyChangeListener listener) {
        manager.addOntologyChangeListener(listener);
        changeListenerManager.recordListenerAdded(listener);
    }


    public void removeOntologyChangeListener(OWLOntologyChangeListener listener) {
        manager.removeOntologyChangeListener(listener);
        changeListenerManager.recordListenerRemoved(listener);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////


    public void addListener(OWLModelManagerListener listener) {
        modelManagerChangeListeners.add(listener);
        modelManagerListenerManager.recordListenerAdded(listener);
    }


    public void removeListener(OWLModelManagerListener listener) {
        modelManagerChangeListeners.remove(listener);
        modelManagerListenerManager.recordListenerRemoved(listener);
    }


    public void fireEvent(EventType type) {
        logger.debug("Firing event {}", type);
        Runnable r = () -> {
            OWLModelManagerChangeEvent event = new OWLModelManagerChangeEvent(this, type);
            logger.debug("Firing model manager event: {}", event);
            for (OWLModelManagerListener listener : new ArrayList<>(modelManagerChangeListeners)) {
                try {
                    listener.handleChange(event);
                } catch (Throwable e) {
                    logger.warn("Exception thrown by listener: {}.  Detatching bad listener.", listener.getClass().getName());
                    modelManagerChangeListeners.remove(listener);
                }
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        }
        else {
            SwingUtilities.invokeLater(r);
        }
    }

    public void addIOListener(IOListener listener) {
        ioListeners.add(listener);
    }


    public void removeIOListener(IOListener listener) {
        ioListeners.remove(listener);
    }


    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Entity rendering classes
    //
    //////////////////////////////////////////////////////////////////////////////////////


    public OWLModelManagerEntityRenderer getOWLEntityRenderer() {
        if (entityRenderer == null) {
            try {
                OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
                RendererPlugin plugin = preferences.getRendererPlugin();
                entityRenderer = plugin.newInstance();
                loadRenderer();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.error(e.getMessage());
            }
            if (entityRenderer == null) {
                entityRenderer = new OWLEntityRendererImpl();
                loadRenderer();
            }
        }
        return entityRenderer;
    }


    public String getRendering(OWLObject object) {
        // Look for a cached version of the rendering first!
        if (object instanceof OWLEntity) {
            AnonymousDefinedClassManager adcManager = get(AnonymousDefinedClassManager.ID);
            if (adcManager != null &&
                    object instanceof OWLClass &&
                    adcManager.isAnonymous((OWLClass) object)) {
                return owlObjectRenderingCache.getRendering(adcManager.getExpression((OWLClass) object), getOWLObjectRenderer());
            }
            else {
                getOWLEntityRenderer();
                String rendering = owlEntityRenderingCache.getRendering((OWLEntity) object);
                if (rendering != null) {
                    return rendering;
                }
                else {
                    return getOWLEntityRenderer().render((OWLEntity) object);
                }
            }
        }

        return owlObjectRenderingCache.getRendering(object, getOWLObjectRenderer());
    }


    public void renderingChanged(OWLEntity entity, final OWLModelManagerEntityRenderer renderer) {
        owlEntityRenderingCache.updateRendering(entity);
        owlObjectRenderingCache.clear();
        // We should inform listeners
        for (OWLModelManagerListener listener : new ArrayList<>(modelManagerChangeListeners)) {
            listener.handleChange(new OWLModelManagerChangeEvent(this, EventType.ENTITY_RENDERING_CHANGED));
        }
    }

    public void refreshRenderer() {
        if (entityRenderer != null) {
            entityRenderer.removeListener(this);
            try {
                entityRenderer.dispose();
            } catch (Exception e) {
                logger.warn("An error occurred whilst disposing of the entity renderer: {}", e);
            }
        }
        entityRenderer = null;
        getOWLEntityRenderer();
        loadRenderer();
    }

    private void loadRenderer() {
        entityRenderer.addListener(this);
        entityRenderer.setup(this);
        entityRenderer.initialise();
        rebuildEntityIndices();
        fireEvent(EventType.ENTITY_RENDERER_CHANGED);
    }


    public void setOWLEntityRenderer(OWLModelManagerEntityRenderer renderer) {
        refreshRenderer();
    }


    public OWLObjectRenderer getOWLObjectRenderer() {
        return objectRenderer;
    }


    public OWLExpressionCheckerFactory getOWLExpressionCheckerFactory() {
        return owlExpressionCheckerFactory;
    }


    public OWLEntityFactory getOWLEntityFactory() {
        if (entityFactory == null) {
            entityFactory = new CustomOWLEntityFactory(this);
        }
        return entityFactory;
    }


    public void setOWLEntityFactory(OWLEntityFactory owlEntityFactory) {
        this.entityFactory = owlEntityFactory;
    }


    public OWLEntityFinder getOWLEntityFinder() {
        if (entityFinder == null) {
            entityFinder = new OWLEntityFinderImpl(this, owlEntityRenderingCache);
        }
        return entityFinder;
    }


    public Comparator<OWLObject> getOWLObjectComparator() {
        OWLObjectComparator<OWLObject> comparator = get(OWL_OBJECT_COMPARATOR_KEY);
        if (comparator == null) {
            comparator = new OWLObjectRenderingComparator<>(this);
            put(OWL_OBJECT_COMPARATOR_KEY, comparator);
        }
        return comparator;
    }


    private void rebuildEntityIndices() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        owlEntityRenderingCache.rebuild();
        owlObjectRenderingCache.clear();
        stopwatch.stop();
        logger.debug("Rebuilt entity indices in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Reasoner
    //
    //////////////////////////////////////////////////////////////////////////////////////


    public OWLReasonerManager getOWLReasonerManager() {
        return owlReasonerManager;
    }


    public OWLReasoner getReasoner() {
        return getOWLReasonerManager().getCurrentReasoner();
    }

    public ReasonerPreferences getReasonerPreferences() {
        return getOWLReasonerManager().getReasonerPreferences();
    }


    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Error handling
    //
    //////////////////////////////////////////////////////////////////////////////////////


    public void setMissingImportHandler(MissingImportHandler missingImportHandler) {
        userResolvedIRIMapper.setMissingImportHandler(missingImportHandler);
    }

    public void setLoadErrorHandler(OntologyLoadErrorHandler handler) {
        this.loadErrorHandler = handler;
    }
}
