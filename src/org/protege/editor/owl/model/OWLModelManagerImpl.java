package org.protege.editor.owl.model;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coode.xml.XMLWriterPreferences;
import org.protege.editor.core.AbstractModelManager;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLModelManagerDescriptor;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCache;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCacheImpl;
import org.protege.editor.owl.model.cache.OWLObjectRenderingCache;
import org.protege.editor.owl.model.description.anonymouscls.AnonymousDefinedClassManager;
import org.protege.editor.owl.model.entity.CustomOWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.find.EntityFinder;
import org.protege.editor.owl.model.find.EntityFinderImpl;
import org.protege.editor.owl.model.hierarchy.OWLHierarchyManager;
import org.protege.editor.owl.model.hierarchy.OWLHierarchyManagerImpl;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.model.history.HistoryManagerImpl;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.OWLReasonerManagerImpl;
import org.protege.editor.owl.model.io.*;
import org.protege.editor.owl.model.library.OntologyLibraryManager;
import org.protege.editor.owl.model.library.folder.FolderOntologyLibrary;
import org.protege.editor.owl.model.repository.OntologyIRIExtractor;
import org.protege.editor.owl.model.selection.ontologies.ActiveOntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.AllLoadedOntologiesSelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.ImportsClosureOntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;
import org.protege.editor.owl.model.util.ListenerManager;
import org.protege.editor.owl.ui.OWLObjectComparator;
import org.protege.editor.owl.ui.clsdescriptioneditor.ManchesterOWLExpressionCheckerFactory;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionCheckerFactory;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandler;
import org.protege.editor.owl.ui.renderer.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.inference.OWLReasoner;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import java.io.File;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URI;
import java.util.*;


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
public class OWLModelManagerImpl extends AbstractModelManager
        implements OWLModelManager, OWLEntityRendererListener, OWLOntologyChangeListener, OWLOntologyLoaderListener {

    private static final Logger logger = Logger.getLogger(OWLModelManagerImpl.class);

    private List<OWLModelManagerListener> modelManagerChangeListeners;

    private ListenerManager<OWLModelManagerListener> modelManagerListenerManager;

    private ListenerManager<OWLOntologyChangeListener> changeListenerManager;

    private List<IOListener> ioListeners;

    private HistoryManager historyManager;

    private OWLModelManagerEntityRenderer entityRenderer;

    private OWLObjectRenderer objectRenderer;

    private OWLOntology activeOntology;

    private Set<File> ontologyRootFolders;

    private OWLEntityRenderingCache owlEntityRenderingCache;

    /**
     * P4 repeatedly asks for the same rendering multiple times in a row
     * because of the components listening to mouse events etc so cache a
     * small number of objects we have just rendered
     */
    private OWLObjectRenderingCache owlObjectRenderingCache;

    private EntityFinder entityFinder;

    private OWLReasonerManager owlReasonerManager;

    private OWLModelManagerDescriptor owlModelManagerDescriptor;

    /**
     * Dirty ontologies are ontologies that have been edited
     * and not saved.
     */
    private Set<OWLOntology> dirtyOntologies;

    /**
     * The <code>OWLConnection</code> that we use to manage
     * ontologies.
     */
    private OWLOntologyManager manager;

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


    public OWLModelManagerImpl() {
        super();

        modelManagerListenerManager = new ListenerManager<OWLModelManagerListener>();
        changeListenerManager = new ListenerManager<OWLOntologyChangeListener>();
        manager = OWLManager.createOWLOntologyManager();
        manager.setSilentMissingImportsHandling(true);
        manager.addOntologyChangeListener(this);
        manager.addOntologyLoaderListener(this);

        // URI mappers for loading - added in reverse order
        autoMappedRepositoryIRIMapper = new AutoMappedRepositoryIRIMapper(this);
        userResolvedIRIMapper = new UserResolvedIRIMapper(new MissingImportHandlerImpl());
        manager.clearIRIMappers();
        manager.addIRIMapper(userResolvedIRIMapper);
        manager.addIRIMapper(new WebConnectionIRIMapper());
        manager.addIRIMapper(new UserRepositoryIRIMapper(this));
        manager.addIRIMapper(autoMappedRepositoryIRIMapper);


        dirtyOntologies = new HashSet<OWLOntology>();
        ontologyRootFolders = new HashSet<File>();
        ontSelectionStrategies = new HashSet<OntologySelectionStrategy>();


        modelManagerChangeListeners = new ArrayList<OWLModelManagerListener>();
        ioListeners = new ArrayList<IOListener>();


        objectRenderer = new OWLObjectRendererImpl(this);
//        uriShortFormProvider = new SimpleURIShortFormProvider();
        owlEntityRenderingCache = new OWLEntityRenderingCacheImpl();
        owlEntityRenderingCache.setOWLModelManager(this);
        owlObjectRenderingCache = new OWLObjectRenderingCache(this);

        owlExpressionCheckerFactory = new ManchesterOWLExpressionCheckerFactory(this);

        activeOntologies = new HashSet<OWLOntology>();

        // force the renderer to be created
        // to prevent double cache rebuild once ontologies loaded
        getOWLEntityRenderer();

        registerOntologySelectionStrategy(new ActiveOntologySelectionStrategy(this));
        registerOntologySelectionStrategy(new AllLoadedOntologiesSelectionStrategy(this));
        registerOntologySelectionStrategy(activeOntologiesStrategy = new ImportsClosureOntologySelectionStrategy(this));

        XMLWriterPreferences.getInstance().setUseNamespaceEntities(XMLWriterPrefs.getInstance().isUseEntities());

//        put(AnonymousDefinedClassManager.ID, new AnonymousDefinedClassManager(this));

        put(OntologySourcesManager.ID, new OntologySourcesManager(this));
    }


    public void dispose() {
        super.dispose();

        OntologySourcesManager sourcesMngr = get(OntologySourcesManager.ID);
        removeIOListener(sourcesMngr);

        try {
            getReasoner().dispose();

            // Empty caches
            owlEntityRenderingCache.dispose();
            owlObjectRenderingCache.dispose();

            if (entityRenderer != null){
                entityRenderer.dispose();
            }

            owlReasonerManager.dispose();
        }
        catch (Exception e) {
            logger.error(e.getMessage() + "\n", e);
        }

        // Name and shame plugins that do not (or can't be bothered to) clean up
        // their listeners!
        modelManagerListenerManager.dumpWarningForAllListeners(logger, Level.ERROR,
                                                               "(Listeners should be removed in the plugin dispose method!)");

        changeListenerManager.dumpWarningForAllListeners(logger, Level.ERROR,
                                                         "(Listeners should be removed in the plugin dispose method!)");
    }


    public boolean isDirty() {
        return !dirtyOntologies.isEmpty();
    }


    public OWLOntologyManager getOWLOntologyManager() {
        return manager;
    }


    public OWLModelManagerDescriptor getOWLModelManagerDescriptor() {
        return owlModelManagerDescriptor;
    }


    public String getId() {
        return "OWLModelManager";
    }


    private OntologyLibraryManager ontologyLibraryManager;


    public OntologyLibraryManager getOntologyLibraryManager() {
        if (ontologyLibraryManager == null) {
            ontologyLibraryManager = new OntologyLibraryManager();
        }
        return ontologyLibraryManager;
    }


    public OWLHierarchyManager getOWLHierarchyManager() {
        OWLHierarchyManager hm = get(OWLHierarchyManager.ID);
        if (hm == null){
            hm = new OWLHierarchyManagerImpl(this);
            put(OWLHierarchyManager.ID, hm);
        }
        return hm;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //
    // Loading
    //
    ///////////////////////////////////////////////////////////////////////////////////////

    public void startedLoadingOntology(LoadingStartedEvent event) {
        System.out.println("loading " + event.getOntologyID() + " from " + event.getPhysicalURI());
        fireBeforeLoadEvent(event.getOntologyID(), event.getPhysicalURI());
    }


    public void finishedLoadingOntology(LoadingFinishedEvent event) {
        if (!event.isSuccessful()){
            Exception e = event.getException();
            if (loadErrorHandler != null){
                try {
                    loadErrorHandler.handleErrorLoadingOntology(event.getOntologyID(),
                                                                event.getPhysicalURI(),
                                                                e);
                }
                catch (Throwable e1) {
                    // if, for any reason, the loadErrorHandler cannot report the error
                    ErrorLogPanel.showErrorDialog(e1);
                }
            }
        }
        fireAfterLoadEvent(event.getOntologyID(), event.getPhysicalURI());
    }


    /**
     * Loads the ontology that has the specified ontology URI.
     * <p/>
     * @param iri The IRI of the ontology to be loaded.  Note
     *            that this is <b>not</b> the physical URI of a document
     *            that contains a representation of the ontology.  The
     *            physical location of any concrete representation of the
     *            ontology is determined by the <code>Repository</code>
     *            mechanism.
     */
    public OWLOntology loadOntology(IRI iri) throws OWLOntologyCreationException {
        OWLOntology ont = null;
        try{
            ont = manager.loadOntology(iri);
            setActiveOntology(ont);
            fireEvent(EventType.ONTOLOGY_LOADED);
        }
        catch (OWLOntologyCreationException e){
            // will be handled by the loadErrorHandler, so ignore
        }
        return ont;
    }


    /**
     * A convenience method that loads an ontology from a file
     * The location of the file is specified by the URI argument.
     */
    public boolean loadOntologyFromPhysicalURI(URI uri) throws OWLOntologyCreationException {
        OWLOntology ontology = null;

        // Obtain the actual ontology IRI.
        // @@TODO handle anonymous ontologies
        IRI ontologyIRI = new OntologyIRIExtractor(uri).getOntologyIRI();

        // if the ontology has already been loaded, we cannot have more than one ont with the same URI
        // in this ontology manager (and therefore workspace)
        if (manager.getOntology(ontologyIRI) != null){
            throw new OWLOntologyCreationException("Not loaded." +
                                                   "\nWorkspace already contains ontology: " + ontologyIRI +
                                                   ".\nPlease open the ontology in a new frame.");
        }
        else{
            // Set up a mapping from the ontology URI to the physical URI
            manager.addIRIMapper(new SimpleIRIMapper(ontologyIRI, uri));
            if (uri.getScheme()  != null && uri.getScheme().equals("file")) {
                // Load the URIs of other ontologies that are contained in the
                // same folder.
                addRootFolder(uri);
                //loadOntologyURIMap(new File(uri).getParentFile());
            }
            // Delegate to the load method using the IRI of the ontology
            ontology = loadOntology(ontologyIRI);

            owlModelManagerDescriptor = new OWLModelManagerDescriptor(uri);
        }
        return ontology != null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    //  Ontology URI to Physical URI mapping
    //
    ////////////////////////////////////////////////////////////////////////////////////////


    private void addRootFolder(URI uri) {
        // Ensure that the URI is a file URI
        if (!uri.getScheme().equals("file")) {
            return;
        }
        File file = new File(uri);
        // Add the parent file which will be the folder
        if (ontologyRootFolders.add(file.getParentFile())) {
            // Add automapped library
            autoMappedRepositoryIRIMapper.addLibrary(new FolderOntologyLibrary(file.getParentFile()));
        }
    }


    public URI getOntologyPhysicalURI(OWLOntology ontology) {
        return manager.getPhysicalURIForOntology(ontology);
    }


    public void setPhysicalURI(OWLOntology ontology, URI physicalURI) {
        manager.setPhysicalURIForOntology(ontology, physicalURI);
    }


    /**
     * Loads the ontology URI map with the ontologies that
     * are found in the specified folder.
     * @param folder The folder that contains documents that
     *               contain ontologies.
     */
    protected void loadOntologyURIMap(File folder) {
        // Search through the files to get any ontologies
        for (File curFile : folder.listFiles(new OntologyFileFilter())) {
            OntologyIRIExtractor ext = new OntologyIRIExtractor(curFile.toURI());
            manager.addIRIMapper(new SimpleIRIMapper(ext.getOntologyIRI(), curFile.toURI()));
            if (logger.isInfoEnabled()) {
                logger.info("Adding auto-mapping: " + ext.getOntologyIRI() + " -> " + curFile.toURI());
            }
        }
    }


    public OWLOntology createNewOntology(OWLOntologyID ontologyID, URI physicalURI) throws OWLOntologyCreationException {
        manager.addIRIMapper(new SimpleIRIMapper(ontologyID.getDefaultDocumentIRI(), physicalURI));
        OWLOntology ont = manager.createOntology(ontologyID);
        dirtyOntologies.add(ont);
        setActiveOntology(ont);
        fireEvent(EventType.ONTOLOGY_CREATED);
        return ont;
    }


    public OWLOntology reload(OWLOntology ont) throws OWLOntologyCreationException {
        manager.removeOntology(ont);
        ont = manager.loadOntologyFromPhysicalURI(getOntologyPhysicalURI(ont));

        fireEvent(EventType.ONTOLOGY_RELOADED);
        setActiveOntology(ont, true);
        return ont;
    }


    public boolean removeOntology(OWLOntology ont) {
        if (manager.contains(ont.getOntologyID()) && manager.getOntologies().size() > 1){

            boolean resetActiveOntologyRequired = ont.equals(activeOntology);
            activeOntologies.remove(ont);
            dirtyOntologies.remove(ont);
            manager.removeOntology(ont);

            if (resetActiveOntologyRequired){
                OWLOntology newActiveOnt = null;
                if (!activeOntologies.isEmpty()){
                    newActiveOnt = activeOntologies.iterator().next();
                }
                if (newActiveOnt == null && !manager.getOntologies().isEmpty()){
                    newActiveOnt = manager.getOntologies().iterator().next();
                }

                setActiveOntology(newActiveOnt, true);
            }
            else{
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


    public void save() throws OWLOntologyStorageException {
        // Save all of the ontologies that are editable and that
        // have been modified.
        for (OWLOntology ont : new HashSet<OWLOntology>(dirtyOntologies)) {
            save(ont);
        }
    }


    public void save(OWLOntology ont) throws OWLOntologyStorageException {
        final URI physicalURI = manager.getPhysicalURIForOntology(ont);

        try{
            fireBeforeSaveEvent(ont.getOntologyID(), physicalURI);

            try {
                if (!physicalURI.getScheme().equals("file")){
                    throw new ProtocolException("Cannot save file to remote location: " + physicalURI);
                }

                // the OWLAPI v3 saves to a temp file now
                manager.saveOntology(ont, manager.getOntologyFormat(ont), physicalURI);

                manager.setPhysicalURIForOntology(ont, physicalURI);
            }
            catch (IOException e) {
                throw new OWLOntologyStorageException("Error while saving ontology " + ont.getOntologyID() + " to " + physicalURI, e);
            }

            logger.info("Saved " + getRendering(ont) + " to " + physicalURI);

            dirtyOntologies.remove(ont);

            fireEvent(EventType.ONTOLOGY_SAVED);
            fireAfterSaveEvent(ont.getOntologyID(), physicalURI);
        }
        catch(OWLOntologyStorageException e){
            if (saveErrorHandler != null){
                try {
                    saveErrorHandler.handleErrorSavingOntology(ont, physicalURI, e);
                }
                catch (Exception e1) {
                    throw new OWLOntologyStorageException(e1);
                }
            }
            else{
                throw e;
            }
        }
    }


    private boolean isTempFileSavingActive() {
        return PreferencesManager.getInstance().getApplicationPreferences(UIUtil.FILE_PREFERENCES_KEY).getBoolean(UIUtil.ENABLE_TEMP_DIRECTORIES_KEY, true);
    }


    public void saveAs() throws OWLOntologyStorageException {
        save();
    }


    public Set<OWLOntology> getOntologies() {
        return manager.getOntologies();
    }


    public Set<OWLOntology> getDirtyOntologies() {
        return new HashSet<OWLOntology>(dirtyOntologies);
    }


    /**
     * Forces the system to believe that an ontology
     * has been modified.
     * @param ontology The ontology to be made dirty.
     */
    public void setDirty(OWLOntology ontology) {
        dirtyOntologies.add(ontology);
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

    //////////////////////////////////////////////////////////////////////////////////////////
    //
    // Various hierarchies
    //
    //////////////////////////////////////////////////////////////////////////////////////////





    /**
     * Sets the active ontology (and hence the set of active ontologies).
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
        logger.info("Setting active ontology to " + activeOntology.getOntologyID());
        rebuildActiveOntologiesCache();
        // Rebuild entity indices
        rebuildEntityIndices();
        // Inform our listeners
        fireEvent(EventType.ACTIVE_ONTOLOGY_CHANGED);
        logger.info("... active ontology changed");
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


    protected void reactToOntologyChange(OWLOntologyChange change) {
        // Reset the dirty flag
        dirtyOntologies.add(change.getOntology());
        // Log the history in the history history
    }


    public void applyChange(OWLOntologyChange change) {
        try {
            AnonymousDefinedClassManager adcManager = get(AnonymousDefinedClassManager.ID);
            if (adcManager != null){
                change = adcManager.getChangeRewriter().rewriteChange(change);
            }
            manager.applyChange(change);
        }
        catch (OWLOntologyChangeException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public void applyChanges(List<? extends OWLOntologyChange> changes) {
        try {
            AnonymousDefinedClassManager adcManager = get(AnonymousDefinedClassManager.ID);
            if (adcManager != null){
                changes = adcManager.getChangeRewriter().rewriteChanges(changes);
            }
            manager.applyChanges(changes);
        }
        catch (OWLOntologyChangeException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
        getHistoryManager().logChanges(changes);
        boolean refreshActiveOntology = false;
        for (OWLOntologyChange change : changes) {
            dirtyOntologies.add(change.getOntology());
            if (change.isImportChange()){
                refreshActiveOntology = true;
            }
        }
        if (refreshActiveOntology){
            setActiveOntology(getActiveOntology(), true);
        }
    }


    public boolean isChangedEntity(OWLEntity entity) {
        return false;
    }


    public HistoryManager getHistoryManager() {
        if (historyManager == null) {
            historyManager = new HistoryManagerImpl(this);
        }
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

    public void addIOListener(IOListener listener) {
        ioListeners.add(listener);
    }


    public void removeIOListener(IOListener listener) {
        ioListeners.remove(listener);
    }

    private void fireBeforeLoadEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for(IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.beforeLoad(new IOListenerEvent(ontologyID, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }

    private void fireAfterLoadEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for(IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.afterLoad(new IOListenerEvent(ontologyID, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }


    private void fireBeforeSaveEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for(IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.beforeSave(new IOListenerEvent(ontologyID, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }


    private void fireAfterSaveEvent(OWLOntologyID ontologyID, URI physicalURI) {
        for(IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.afterSave(new IOListenerEvent(ontologyID, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Entity rendering classes
    //
    //////////////////////////////////////////////////////////////////////////////////////


    public OWLModelManagerEntityRenderer getOWLEntityRenderer() {
        if (entityRenderer == null) {
            try {
                String clsName = OWLRendererPreferences.getInstance().getRendererClass();
                Class c = Class.forName(clsName);
                OWLModelManagerEntityRenderer ren = (OWLModelManagerEntityRenderer) c.newInstance();
                // Force an update by using the setter method.
                setOWLEntityRenderer(ren);
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
                setOWLEntityRenderer(new OWLEntityRendererImpl());
            }
        }
        return entityRenderer;
    }


    public String getRendering(OWLObject object) {
        // Look for a cached version of the rending first!
        if (object instanceof OWLEntity) {
            AnonymousDefinedClassManager adcManager = get(AnonymousDefinedClassManager.ID);
            if (adcManager != null &&
                object instanceof OWLClass &&
                adcManager.isAnonymous((OWLClass)object)){
                return owlObjectRenderingCache.getRendering(adcManager.getExpression((OWLClass)object), getOWLObjectRenderer());
            }
            else{
                getOWLEntityRenderer();
                String rendering = owlEntityRenderingCache.getRendering((OWLEntity) object);
                if(rendering != null) {
                    return rendering;
                }
                else {
                    return getOWLEntityRenderer().render((OWLEntity) object);
                }
            }
        }

        return owlObjectRenderingCache.getRendering(object, getOWLObjectRenderer());
    }


    public void renderingChanged(OWLEntity entity, final OWLEntityRenderer renderer) {
        owlEntityRenderingCache.updateRendering(entity);
        owlObjectRenderingCache.clear();
        // We should inform listeners
        for (OWLModelManagerListener listener : new ArrayList<OWLModelManagerListener>(modelManagerChangeListeners)) {
            listener.handleChange(new OWLModelManagerChangeEvent(this, EventType.ENTITY_RENDERING_CHANGED));
        }
    }


    public void setOWLEntityRenderer(OWLModelManagerEntityRenderer renderer) {
        if (entityRenderer != null) {
            entityRenderer.removeListener(this);
            try {
                entityRenderer.dispose();
            }
            catch (Exception e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
        entityRenderer = renderer;
        entityRenderer.addListener(this);
        entityRenderer.setup(this);
        entityRenderer.initialise();
        rebuildEntityIndices();
        fireEvent(EventType.ENTITY_RENDERER_CHANGED);
    }


    public OWLObjectRenderer getOWLObjectRenderer() {
        return objectRenderer;
    }


    public OWLExpressionCheckerFactory getOWLExpressionCheckerFactory() {
        return owlExpressionCheckerFactory;
    }

    private static final String ESCAPE_CHAR = "'";

    public OWLClass getOWLClass(String rendering) {
        OWLClass cls = owlEntityRenderingCache.getOWLClass(rendering);
        if (cls == null && !rendering.startsWith(ESCAPE_CHAR) && !rendering.endsWith(ESCAPE_CHAR)){
            cls = owlEntityRenderingCache.getOWLClass(ESCAPE_CHAR + rendering + ESCAPE_CHAR);
        }
        return cls;
    }


    public OWLObjectProperty getOWLObjectProperty(String rendering) {
        OWLObjectProperty prop = owlEntityRenderingCache.getOWLObjectProperty(rendering);
        if (prop == null && !rendering.startsWith(ESCAPE_CHAR) && !rendering.endsWith(ESCAPE_CHAR)){
            prop = owlEntityRenderingCache.getOWLObjectProperty(ESCAPE_CHAR + rendering + ESCAPE_CHAR);
        }
        return prop;
    }


    public OWLDataProperty getOWLDataProperty(String rendering) {
        OWLDataProperty prop = owlEntityRenderingCache.getOWLDataProperty(rendering);
        if (prop == null && !rendering.startsWith(ESCAPE_CHAR) && !rendering.endsWith(ESCAPE_CHAR)){
            prop = owlEntityRenderingCache.getOWLDataProperty(ESCAPE_CHAR + rendering + ESCAPE_CHAR);
        }
        return prop;
    }


    public OWLAnnotationProperty getOWLAnnotationProperty(String rendering) {
        OWLAnnotationProperty prop = owlEntityRenderingCache.getOWLAnnotationProperty(rendering);
        if (prop == null && !rendering.startsWith(ESCAPE_CHAR) && !rendering.endsWith(ESCAPE_CHAR)){
            prop = owlEntityRenderingCache.getOWLAnnotationProperty(ESCAPE_CHAR + rendering + ESCAPE_CHAR);
        }
        return prop;
    }


    public OWLNamedIndividual getOWLIndividual(String rendering) {
        OWLNamedIndividual individual = owlEntityRenderingCache.getOWLIndividual(rendering);
        if (individual == null && !rendering.startsWith(ESCAPE_CHAR) && !rendering.endsWith(ESCAPE_CHAR)){
            individual = owlEntityRenderingCache.getOWLIndividual(ESCAPE_CHAR + rendering + ESCAPE_CHAR);
        }
        return individual;
    }


    public OWLDatatype getOWLDatatype(String rendering) {
        OWLDatatype dataType = owlEntityRenderingCache.getOWLDatatype(rendering);
        if (dataType == null && !rendering.startsWith(ESCAPE_CHAR) && !rendering.endsWith(ESCAPE_CHAR)){
            dataType = owlEntityRenderingCache.getOWLDatatype(ESCAPE_CHAR + rendering + ESCAPE_CHAR);
        }
        return dataType;
    }


    public OWLEntity getOWLEntity(String rendering) {
        OWLEntity entity = owlEntityRenderingCache.getOWLEntity(rendering);
        if (entity == null && !rendering.startsWith(ESCAPE_CHAR) && !rendering.endsWith(ESCAPE_CHAR)){
            entity = owlEntityRenderingCache.getOWLEntity(ESCAPE_CHAR + rendering + ESCAPE_CHAR);
        }
        return entity;
    }


    public Set<String> getOWLEntityRenderings() {
        return owlEntityRenderingCache.getOWLEntityRenderings();
    }


    public OWLEntityFactory getOWLEntityFactory() {
        if (entityFactory == null){
            entityFactory = new CustomOWLEntityFactory(this);
        }
        return entityFactory;
    }


    public void setOWLEntityFactory(OWLEntityFactory owlEntityFactory) {
        this.entityFactory = owlEntityFactory;
    }


    public EntityFinder getEntityFinder() {
        if (entityFinder == null){
            entityFinder = new EntityFinderImpl(this, owlEntityRenderingCache);
        }
        return entityFinder;
    }


    public void rebuildEntityIndices() {
        logger.info("Rebuilding entity indices...");
        long t0 = System.currentTimeMillis();
        owlEntityRenderingCache.rebuild();
        owlObjectRenderingCache.clear();
        logger.info("... rebuilt in " + (System.currentTimeMillis() - t0) + " ms");
    }


    public <T extends OWLObject> Comparator<T> getOWLObjectComparator(){
        OWLObjectComparator<T> comparator = get(OWL_OBJECT_COMPARATOR_KEY);
        if (comparator == null){
            comparator = new OWLObjectComparator<T>(this);
            put(OWL_OBJECT_COMPARATOR_KEY, comparator);
        }
        return comparator;
    }


    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Reasoner
    //
    //////////////////////////////////////////////////////////////////////////////////////


    public OWLReasonerManager getOWLReasonerManager() {
        if (owlReasonerManager == null) {
            owlReasonerManager = new OWLReasonerManagerImpl(this);
        }
        return owlReasonerManager;
    }


    public OWLReasoner getReasoner() {
        return getOWLReasonerManager().getCurrentReasoner();
    }


    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Error handling
    //
    //////////////////////////////////////////////////////////////////////////////////////


    public void setMissingImportHandler(MissingImportHandler missingImportHandler) {
        userResolvedIRIMapper.setMissingImportHandler(missingImportHandler);
    }


    public void setSaveErrorHandler(SaveErrorHandler handler) {
        this.saveErrorHandler = handler;
    }


    public void setLoadErrorHandler(OntologyLoadErrorHandler handler) {
        this.loadErrorHandler = handler;
    }





    //////////////////////////////////////////////////////////////////////////////////////
    //
    //  Deprecated
    //
    //////////////////////////////////////////////////////////////////////////////////////


    public OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
        return getOWLHierarchyManager().getOWLClassHierarchyProvider();
    }


    public OWLObjectHierarchyProvider<OWLClass> getInferredOWLClassHierarchyProvider() {
        return getOWLHierarchyManager().getInferredOWLClassHierarchyProvider();
    }


    public OWLObjectHierarchyProvider<OWLObjectProperty> getOWLObjectPropertyHierarchyProvider() {
        return getOWLHierarchyManager().getOWLObjectPropertyHierarchyProvider();
    }


    public OWLObjectHierarchyProvider<OWLDataProperty> getOWLDataPropertyHierarchyProvider() {
        return getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider();
    }


    public void rebuildOWLClassHierarchy() {
        throw new RuntimeException("mngr.rebuildOWLClassHierarchy not implemented");
    }


    public void rebuildOWLObjectPropertyHierarchy() {
        throw new RuntimeException("mngr.rebuildOWLObjectPropertyHierarchy not implemented");
    }


    public void rebuildOWLDataPropertyHierarchy() {
        throw new RuntimeException("mngr.rebuildOWLDataPropertyHierarchy not implemented");
    }


    public List<OWLClass> getMatchingOWLClasses(String renderingStart) {
        List<OWLClass> orderedResults = new ArrayList<OWLClass>(getEntityFinder().getMatchingOWLClasses(renderingStart + "*", false));
        Collections.sort(orderedResults, getOWLObjectComparator());
        return orderedResults;
    }


    public List<OWLObjectProperty> getMatchingOWLObjectProperties(String renderingStart) {
        List<OWLObjectProperty> orderedResults = new ArrayList<OWLObjectProperty>(getEntityFinder().getMatchingOWLObjectProperties(renderingStart + "*", false));
        Collections.sort(orderedResults, getOWLObjectComparator());
        return orderedResults;
    }


    public List<OWLDataProperty> getMatchingOWLDataProperties(String renderingStart) {
        List<OWLDataProperty> orderedResults = new ArrayList<OWLDataProperty>(getEntityFinder().getMatchingOWLDataProperties(renderingStart + "*", false));
        Collections.sort(orderedResults, getOWLObjectComparator());
        return orderedResults;
    }


    public List<OWLIndividual> getMatchingOWLIndividuals(String renderingStart) {
        List<OWLIndividual> orderedResults = new ArrayList<OWLIndividual>(getEntityFinder().getMatchingOWLIndividuals(renderingStart + "*", false));
        Collections.sort(orderedResults, getOWLObjectComparator());
        return orderedResults;
    }


    public List<OWLDatatype> getMatchingOWLDatatypes(String renderingStart) {
        List<OWLDatatype> orderedResults = new ArrayList<OWLDatatype>(getEntityFinder().getMatchingOWLDatatypes(renderingStart + "*", false));
        Collections.sort(orderedResults, getOWLObjectComparator());
        return orderedResults;
    }
}
