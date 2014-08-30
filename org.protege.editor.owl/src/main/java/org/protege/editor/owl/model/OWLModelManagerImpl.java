package org.protege.editor.owl.model;

import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.protege.editor.core.AbstractModelManager;
import org.protege.editor.core.ProtegeApplication;
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
import org.protege.editor.owl.model.io.AutoMappedRepositoryIRIMapper;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.protege.editor.owl.model.io.OntologySourcesManager;
import org.protege.editor.owl.model.io.UserResolvedIRIMapper;
import org.protege.editor.owl.model.io.WebConnectionIRIMapper;
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
import org.protege.editor.owl.ui.renderer.OWLEntityRendererImpl;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererListener;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.protege.editor.owl.ui.renderer.OWLObjectRendererImpl;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;
import org.protege.owlapi.apibinding.ProtegeOWLManager;
import org.protege.owlapi.model.ProtegeOWLOntologyManager;
import org.protege.xmlcatalog.XMLCatalog;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.model.SetOntologyID;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.XMLWriterPreferences;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;


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
public class OWLModelManagerImpl extends AbstractModelManager implements OWLModelManager, OWLEntityRendererListener, OWLOntologyChangeListener, OWLOntologyLoaderListener {

    private static final Logger logger = Logger.getLogger(OWLModelManagerImpl.class);

    private HistoryManager historyManager;

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

    private OWLReasonerManager owlReasonerManager;

    /**
     * Dirty ontologies are ontologies that have been edited
     * and not saved.
     */
    private Set<OWLOntologyID> dirtyOntologies;

    /**
     * The <code>OWLConnection</code> that we use to manage
     * ontologies.
     */
    private ProtegeOWLOntologyManager manager;

    private OntologyCatalogManager ontologyLibraryManager;

    private ExplanationManager explanationManager;

    private OWLEntityFactory entityFactory;

    /**
     * A cache for the imports closure.  Originally, we just requested this
     * each time from the OWLOntologyManager, but this proved to be expensive
     * in terms of time.
     */
    private Set<OWLOntology> activeOntologies;

    private Set<OntologySelectionStrategy> ontSelectionStrategies;

    private OntologySelectionStrategy activeOntologiesStrategy;

    private OWLExpressionCheckerFactory owlExpressionCheckerFactory;


    // error handlers

    private SaveErrorHandler saveErrorHandler;

    private OntologyLoadErrorHandler loadErrorHandler;

    private AutoMappedRepositoryIRIMapper autoMappedRepositoryIRIMapper;

    private UserResolvedIRIMapper userResolvedIRIMapper;


    // listeners

    private List<OWLModelManagerListener> modelManagerChangeListeners;

    private ListenerManager<OWLModelManagerListener> modelManagerListenerManager;

    private ListenerManager<OWLOntologyChangeListener> changeListenerManager;

    private List<IOListener> ioListeners;


    public OWLModelManagerImpl() {
        super();

        modelManagerListenerManager = new ListenerManager<OWLModelManagerListener>();
        changeListenerManager = new ListenerManager<OWLOntologyChangeListener>();
        manager = ProtegeOWLManager.createOWLOntologyManager();
        manager.setUseWriteSafety(true);
        manager.setUseSwingThread(true);
        manager.setOntologyLoaderConfiguration(manager
                .getOntologyLoaderConfiguration()
                .setMissingImportHandlingStrategy(
                        MissingImportHandlingStrategy.SILENT));
        manager.addOntologyChangeListener(this);
        manager.addOntologyLoaderListener(this);


        // URI mappers for loading - added in reverse order
        autoMappedRepositoryIRIMapper = new AutoMappedRepositoryIRIMapper(this);
        userResolvedIRIMapper = new UserResolvedIRIMapper(new MissingImportHandlerImpl());
        manager.clearIRIMappers();
        manager.addIRIMapper(userResolvedIRIMapper);
        manager.addIRIMapper(new WebConnectionIRIMapper());
        manager.addIRIMapper(autoMappedRepositoryIRIMapper);


        dirtyOntologies = new HashSet<OWLOntologyID>();
        ontSelectionStrategies = new HashSet<OntologySelectionStrategy>();


        modelManagerChangeListeners = new ArrayList<OWLModelManagerListener>();
        ioListeners = new ArrayList<IOListener>();


        objectRenderer = new OWLObjectRendererImpl(this);
        owlEntityRenderingCache = new OWLEntityRenderingCacheImpl();
        owlEntityRenderingCache.setOWLModelManager(this);
        owlObjectRenderingCache = new OWLObjectRenderingCache(this);

        owlExpressionCheckerFactory = new ManchesterOWLExpressionCheckerFactory(this);

        activeOntologies = new HashSet<OWLOntology>();

        //needs to be initialized
        activeOntologiesStrategy = new ImportsClosureOntologySelectionStrategy(this);

        // force the renderer to be created
        // to prevent double cache rebuild once ontologies loaded
        getOWLEntityRenderer();

        XMLWriterPreferences.getInstance().setUseNamespaceEntities(XMLWriterPrefs.getInstance().isUseEntities());

//        put(AnonymousDefinedClassManager.ID, new AnonymousDefinedClassManager(this));

        put(OntologySourcesManager.ID, new OntologySourcesManager(this));


    }


    @Override
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
        }
        catch (Exception e) {
            logger.error(e.getMessage() + "\n", e);
        }

        // Name and shame plugins that do not (or can't be bothered to) clean up
        // their listeners!
        modelManagerListenerManager.dumpWarningForAllListeners(logger, Level.ERROR, "(Listeners should be removed in the plugin dispose method!)");

        changeListenerManager.dumpWarningForAllListeners(logger, Level.ERROR, "(Listeners should be removed in the plugin dispose method!)");
    }


    @Override
    public boolean isDirty() {
        return !dirtyOntologies.isEmpty();
    }

    @Override
    public boolean isDirty(OWLOntology ontology) {
        return dirtyOntologies.contains(ontology.getOntologyID());
    }

    @Override
    public void setClean(OWLOntology ontology) {
        dirtyOntologies.remove(ontology.getOntologyID());
    }


    @Override
    public ProtegeOWLOntologyManager getOWLOntologyManager() {
        return manager;
    }


    @Override
    public OntologyCatalogManager getOntologyCatalogManager() {
        if (ontologyLibraryManager == null) {
            ontologyLibraryManager = new OntologyCatalogManager();
        }
        return ontologyLibraryManager;
    }


    @Override
    public OWLHierarchyManager getOWLHierarchyManager() {
        OWLHierarchyManager hm = get(OWLHierarchyManager.ID);
        if (hm == null) {
            hm = new OWLHierarchyManagerImpl(this);
            put(OWLHierarchyManager.ID, hm);
        }
        return hm;
    }

    @Override
    public ExplanationManager getExplanationManager() {
        return explanationManager;
    }

    @Override
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
        if (UIUtil.isLocalFile(uri)) {
            // Load the URIs of other ontologies that are contained in the same folder.
            File parentFile = new File(uri).getParentFile();
            logger.info("Adding root folder: " + parentFile + " ...");
            addRootFolder(parentFile);
            logger.info("\t...done");
        }
        OWLOntology ontology = null;
        try {
            ontology = manager.loadOntologyFromOntologyDocument(IRI.create(uri));
            setActiveOntology(ontology);
            fireEvent(EventType.ONTOLOGY_LOADED);
            OWLOntologyID id = ontology.getOntologyID();
            if (!id.isAnonymous()) {
                manager.addIRIMapper(new SimpleIRIMapper(id
                        .getDefaultDocumentIRI().get(), IRI.create(uri)));
            }
        }
        catch (OWLOntologyCreationException ooce) {
            ;             // will be handled by the loadErrorHandler, so ignore
        }
        return ontology != null;
    }


    @Override
    public void startedLoadingOntology(LoadingStartedEvent event) {
        logger.info("loading " + event.getOntologyID() + " from " + event.getDocumentIRI());
        fireBeforeLoadEvent(event.getOntologyID(), event.getDocumentIRI().toURI());
    }


    @Override
    public void finishedLoadingOntology(LoadingFinishedEvent event) {
        if (!event.isSuccessful()) {
            Exception e = event.getException();
            if (loadErrorHandler != null) {
                try {
                    loadErrorHandler.handleErrorLoadingOntology(event.getOntologyID(), event.getDocumentIRI().toURI(), e);
                }
                catch (Throwable e1) {
                    // if, for any reason, the loadErrorHandler cannot report the error
                    ErrorLogPanel.showErrorDialog(e1);
                }
            }
        }
        fireAfterLoadEvent(event.getOntologyID(), event.getDocumentIRI().toURI());
    }

    @Override
    public XMLCatalog addRootFolder(File dir) {
        return ontologyLibraryManager.addFolder(dir);
    }

    private void fireBeforeLoadEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for (IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.beforeLoad(new IOListenerEvent(ontologyID, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }


    private void fireAfterLoadEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for (IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.afterLoad(new IOListenerEvent(ontologyID, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Ontology URI to Physical URI mapping
    //
    ////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public URI getOntologyPhysicalURI(OWLOntology ontology) {
        IRI ontologyDocumentIRI = manager.getOntologyDocumentIRI(ontology);
        if (ontologyDocumentIRI != null) {
            if (isDefaultOWLAPIDocumentIRI(ontologyDocumentIRI)) {
                return URI.create("");
            }
            else {
                return ontologyDocumentIRI.toURI();
            }
        }
        else {
            return URI.create("");
        }
    }

    private boolean isDefaultOWLAPIDocumentIRI(IRI iri) {
        URI uri = iri.toURI();
        String scheme = uri.getScheme();
        return scheme != null && scheme.equals("owlapi");
    }

    @Override
    public void setPhysicalURI(OWLOntology ontology, URI physicalURI) {
        manager.setOntologyDocumentIRI(ontology, IRI.create(physicalURI));
    }


    @Override
    public OWLOntology createNewOntology(OWLOntologyID ontologyID, URI physicalURI) throws OWLOntologyCreationException {
        if (physicalURI != null) {
            manager.addIRIMapper(new SimpleIRIMapper(ontologyID
                    .getDefaultDocumentIRI().get(), IRI.create(physicalURI)));
        }
        OWLOntology ont = manager.createOntology(ontologyID);
        setActiveOntology(ont);
        if (physicalURI != null) {
            try {
                File containingDirectory = new File(physicalURI).getParentFile();
                if (containingDirectory.exists()) {
                    getOntologyCatalogManager().addFolder(containingDirectory);
                }
            }
            catch (IllegalArgumentException iae) {
                logger.info("Cannot generate ontology catalog for ontology at " + physicalURI);
            }
        }
        fireEvent(EventType.ONTOLOGY_CREATED);
        return ont;
    }


    @Override
    public OWLOntology reload(OWLOntology ont) throws OWLOntologyCreationException {
        IRI ontologyDocumentIRI = IRI.create(getOntologyPhysicalURI(ont));
        manager.removeOntology(ont);
        boolean wasTheActiveOntology = false;
        if (ont.equals(activeOntology)) {
            wasTheActiveOntology = true;
            activeOntology = null;
        }
        dirtyOntologies.remove(ont.getOntologyID());
        try {
            ont = manager.loadOntologyFromOntologyDocument(ontologyDocumentIRI);
        }
        catch (Throwable t) {
            ((OWLOntologyManagerImpl) manager).ontologyCreated(ont);  // put it back - a hack but it works
            manager.setOntologyDocumentIRI(ont, ontologyDocumentIRI);
            throw t instanceof OWLOntologyCreationException ? (OWLOntologyCreationException) t : new OWLOntologyCreationException(t);
        }
        if (wasTheActiveOntology) {
            activeOntology = ont;
        }
        rebuildActiveOntologiesCache();
        refreshRenderer();
        fireEvent(EventType.ONTOLOGY_RELOADED);
        return ont;
    }


    @Override
    public boolean removeOntology(OWLOntology ont) {
        if (manager.contains(ont.getOntologyID()) && manager.getOntologies().size() > 1) {

            boolean resetActiveOntologyRequired = ont.equals(activeOntology);
            activeOntologies.remove(ont);
            dirtyOntologies.remove(ont.getOntologyID());
            manager.removeOntology(ont);

            if (resetActiveOntologyRequired) {
                OWLOntology newActiveOnt = null;
                if (!activeOntologies.isEmpty()) {
                    newActiveOnt = activeOntologies.iterator().next();
                }
                if (newActiveOnt == null && !manager.getOntologies().isEmpty()) {
                    newActiveOnt = manager.getOntologies().iterator().next();
                }

                setActiveOntology(newActiveOnt, true);
            }
            else {
                setActiveOntology(activeOntology, true);
            }
            return true;
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Saving
    //
    ///////////////////////////////////////////////////////////////////////////////////////


    /**
     * Save all of the ontologies that are editable and that have been modified.
     */
    @Override
    public void save() throws OWLOntologyStorageException {
        for (OWLOntologyID ontId : new HashSet<OWLOntologyID>(dirtyOntologies)) {
            if (manager.contains(ontId)) {
                save(manager.getOntology(ontId));
            }
            else {
                dirtyOntologies.remove(ontId);
            }
        }
    }


    @Override
    public void save(OWLOntology ont) throws OWLOntologyStorageException {
        final URI physicalURI = manager.getOntologyDocumentIRI(ont).toURI();

        try {
            fireBeforeSaveEvent(ont.getOntologyID(), physicalURI);

            try {
                if (!UIUtil.isLocalFile(physicalURI)) {
                    throw new ProtocolException("Cannot save file to remote location: " + physicalURI);
                }

                OWLDocumentFormat format = manager.getOntologyFormat(ont);
                /*
                 * Using the addMissingTypes call here for RDF/XML files can result in OWL Full output
                 * and can also result in data corruption.
                 * 
                 * See http://protegewiki.stanford.edu/wiki/OWL2RDFParserDeclarationRequirement
                 */
                manager.saveOntology(ont, format, IRI.create(physicalURI));

                manager.setOntologyDocumentIRI(ont, IRI.create(physicalURI));
            }
            catch (IOException e) {
                throw new OWLOntologyStorageException("Error while saving ontology " + ont.getOntologyID() + " to " + physicalURI, e);
            }

            logger.info("Saved " + getRendering(ont) + " to " + physicalURI);

            dirtyOntologies.remove(ont.getOntologyID());

            fireEvent(EventType.ONTOLOGY_SAVED);
            fireAfterSaveEvent(ont.getOntologyID(), physicalURI);
        }
        catch (OWLOntologyStorageException e) {
            if (saveErrorHandler != null) {
                try {
                    saveErrorHandler.handleErrorSavingOntology(ont, physicalURI, e);
                }
                catch (Exception e1) {
                    throw new OWLOntologyStorageException(e1);
                }
            }
            else {
                throw e;
            }
        }
    }

    /**
     * @throws OWLOntologyStorageException if a problem occurs during the save
     * @deprecated - this method would require user interaction - use <code>OWLEditorKit.saveAs()</code> instead
     */
    @Override
    @Deprecated
    public void saveAs() throws OWLOntologyStorageException {
        save();
    }


    private void fireBeforeSaveEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for (IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.beforeSave(new IOListenerEvent(ontologyID, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }


    private void fireAfterSaveEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for (IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.afterSave(new IOListenerEvent(ontologyID, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Ontology Management
    //
    ///////////////////////////////////////////////////////////////////////////////////////


    @Override
    public Set<OWLOntology> getOntologies() {
        return manager.getOntologies();
    }


    @Override
    public Set<OWLOntology> getDirtyOntologies() {
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>();
        for (OWLOntologyID ontId : new ArrayList<OWLOntologyID>(dirtyOntologies)) {
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
     * @param ontology The ontology to be made dirty.
     */
    @Override
    public void setDirty(OWLOntology ontology) {
        dirtyOntologies.add(ontology.getOntologyID());
    }


    @Override
    public OWLOntology getActiveOntology() {
        return activeOntology;
    }


    @Override
    public OWLDataFactory getOWLDataFactory() {
        return manager.getOWLDataFactory();
    }


    @Override
    public Set<OWLOntology> getActiveOntologies() {
        return activeOntologies;
    }


    @Override
    public boolean isActiveOntologyMutable() {
        return isMutable(getActiveOntology());
    }


    @Override
    public boolean isMutable(OWLOntology ontology) {
        // Assume all ontologies are editable - even ones
        // that have been loaded from non-editable locations e.g.
        // the web.  The reason for this is that feedback from users
        // has indicated that it is a pain when an ontology isn't editable
        // just because it has been downloaded from a web because
        // they can't experiment with adding or removing axioms.
        return true;
    }


    @Override
    public void setActiveOntology(OWLOntology activeOntology) {
        setActiveOntology(activeOntology, false);
    }


    @Override
    public void setActiveOntologiesStrategy(OntologySelectionStrategy strategy) {
        activeOntologiesStrategy = strategy;
        setActiveOntology(getActiveOntology(), true);
        fireEvent(EventType.ONTOLOGY_VISIBILITY_CHANGED);
    }


    @Override
    public OntologySelectionStrategy getActiveOntologiesStrategy() {
        return activeOntologiesStrategy;
    }


    @Override
    public Set<OntologySelectionStrategy> getActiveOntologiesStrategies() {
        return ontSelectionStrategies;
    }


    /**
     * Sets the active ontology (and hence the set of active ontologies).
     * @param activeOntology The ontology to be set as the active ontology.
     * @param force By default, if the specified ontology is already the
     * active ontology then no changes will take place.  This flag can be
     * used to force the active ontology to be reset and listeners notified
     * of a change in the state of the active ontology.
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
        logger.info("Setting active ontology to " + activeOntology.getOntologyID());
        rebuildActiveOntologiesCache();
        // Rebuild entity indices
        entityRenderer.ontologiesChanged();
        rebuildEntityIndices();
        // Inform our listeners
        fireEvent(EventType.ACTIVE_ONTOLOGY_CHANGED);
        logger.info("... active ontology changed");
    }


    @Override
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


    @Override
    public void applyChange(OWLOntologyChange change) {
        try {
            AnonymousDefinedClassManager adcManager = get(AnonymousDefinedClassManager.ID);
            if (adcManager != null) {
                change = adcManager.getChangeRewriter().rewriteChange(change);
            }
            manager.applyChange(change);
        }
        catch (OWLOntologyChangeException e) {
            throw new OWLRuntimeException(e);
        }
    }


    @Override
    public void applyChanges(List<? extends OWLOntologyChange> changes) {
        try {
            AnonymousDefinedClassManager adcManager = get(AnonymousDefinedClassManager.ID);
            if (adcManager != null) {
                changes = adcManager.getChangeRewriter().rewriteChanges(changes);
            }
            manager.applyChanges(changes);
        }
        catch (OWLOntologyChangeException e) {
            throw new OWLRuntimeException(e);
        }
    }


    @Override
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
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


    @Override
    public boolean isChangedEntity(OWLEntity entity) {
        return false;
    }


    @Override
    public HistoryManager getHistoryManager() {
        if (historyManager == null) {
            historyManager = new HistoryManagerImpl(this);
        }
        return historyManager;
    }


    @Override
    public void addOntologyChangeListener(OWLOntologyChangeListener listener) {
        manager.addOntologyChangeListener(listener);
        changeListenerManager.recordListenerAdded(listener);
    }


    @Override
    public void removeOntologyChangeListener(OWLOntologyChangeListener listener) {
        manager.removeOntologyChangeListener(listener);
        changeListenerManager.recordListenerRemoved(listener);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void addListener(OWLModelManagerListener listener) {
        modelManagerChangeListeners.add(listener);
        modelManagerListenerManager.recordListenerAdded(listener);
    }


    @Override
    public void removeListener(OWLModelManagerListener listener) {
        modelManagerChangeListeners.remove(listener);
        modelManagerListenerManager.recordListenerRemoved(listener);
    }


    @Override
    public void fireEvent(EventType type) {
        OWLModelManagerChangeEvent event = new OWLModelManagerChangeEvent(this, type);
        for (OWLModelManagerListener listener : new ArrayList<OWLModelManagerListener>(modelManagerChangeListeners)) {
            try {
                listener.handleChange(event);
            }
            catch (Throwable e) {
                logger.warn("Exception thrown by listener: " + listener.getClass().getName() + ".  Detatching bad listener!");
                ProtegeApplication.getErrorLog().logError(e);
                modelManagerChangeListeners.remove(listener);
            }
        }
    }

    @Override
    public void addIOListener(IOListener listener) {
        ioListeners.add(listener);
    }


    @Override
    public void removeIOListener(IOListener listener) {
        ioListeners.remove(listener);
    }


    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Entity rendering classes
    //
    //////////////////////////////////////////////////////////////////////////////////////


    @Override
    public OWLModelManagerEntityRenderer getOWLEntityRenderer() {
        if (entityRenderer == null) {
            try {
                OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
                RendererPlugin plugin = preferences.getRendererPlugin();
                entityRenderer = plugin.newInstance();
                loadRenderer();
            }
            catch (ClassNotFoundException e) {
                logger.error(e.getMessage());
            }
            catch (InstantiationException e) {
                logger.error(e.getMessage());
            }
            catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
            if (entityRenderer == null) {
                entityRenderer = new OWLEntityRendererImpl();
                loadRenderer();
            }
        }
        return entityRenderer;
    }


    @Override
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


    @Override
    public void renderingChanged(OWLEntity entity, final OWLModelManagerEntityRenderer renderer) {
        owlEntityRenderingCache.updateRendering(entity);
        owlObjectRenderingCache.clear();
        // We should inform listeners
        for (OWLModelManagerListener listener : new ArrayList<OWLModelManagerListener>(modelManagerChangeListeners)) {
            listener.handleChange(new OWLModelManagerChangeEvent(this, EventType.ENTITY_RENDERING_CHANGED));
        }
    }

    @Override
    public void refreshRenderer() {
        if (entityRenderer != null) {
            entityRenderer.removeListener(this);
            try {
                entityRenderer.dispose();
            }
            catch (Exception e) {
                ProtegeApplication.getErrorLog().logError(e);
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


    @Override
    public void setOWLEntityRenderer(OWLModelManagerEntityRenderer renderer) {
        refreshRenderer();
    }


    @Override
    public OWLObjectRenderer getOWLObjectRenderer() {
        return objectRenderer;
    }


    @Override
    public OWLExpressionCheckerFactory getOWLExpressionCheckerFactory() {
        return owlExpressionCheckerFactory;
    }


    @Override
    public OWLEntityFactory getOWLEntityFactory() {
        if (entityFactory == null) {
            entityFactory = new CustomOWLEntityFactory(this);
        }
        return entityFactory;
    }


    @Override
    public void setOWLEntityFactory(OWLEntityFactory owlEntityFactory) {
        entityFactory = owlEntityFactory;
    }


    @Override
    public OWLEntityFinder getOWLEntityFinder() {
        if (entityFinder == null) {
            entityFinder = new OWLEntityFinderImpl(this, owlEntityRenderingCache);
        }
        return entityFinder;
    }


    @Override
    public Comparator<OWLObject> getOWLObjectComparator() {
        OWLObjectComparator<OWLObject> comparator = get(OWL_OBJECT_COMPARATOR_KEY);
        if (comparator == null) {
            comparator = new OWLObjectRenderingComparator<OWLObject>(this);
            put(OWL_OBJECT_COMPARATOR_KEY, comparator);
        }
        return comparator;
    }


    private void rebuildEntityIndices() {
        logger.info("Rebuilding entity indices...");
        long t0 = System.currentTimeMillis();
        owlEntityRenderingCache.rebuild();
        owlObjectRenderingCache.clear();
        logger.info("... rebuilt in " + (System.currentTimeMillis() - t0) + " ms");
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Reasoner
    //
    //////////////////////////////////////////////////////////////////////////////////////


    @Override
    public OWLReasonerManager getOWLReasonerManager() {
        if (owlReasonerManager == null) {
            owlReasonerManager = new OWLReasonerManagerImpl(this);
        }
        return owlReasonerManager;
    }


    @Override
    public OWLReasoner getReasoner() {
        return getOWLReasonerManager().getCurrentReasoner();
    }

    @Override
    public ReasonerPreferences getReasonerPreferences() {
        return getOWLReasonerManager().getReasonerPreferences();
    }


    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Error handling
    //
    //////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void setMissingImportHandler(MissingImportHandler missingImportHandler) {
        userResolvedIRIMapper.setMissingImportHandler(missingImportHandler);
    }


    @Override
    public void setSaveErrorHandler(SaveErrorHandler handler) {
        saveErrorHandler = handler;
    }


    @Override
    public void setLoadErrorHandler(OntologyLoadErrorHandler handler) {
        loadErrorHandler = handler;
    }
}
