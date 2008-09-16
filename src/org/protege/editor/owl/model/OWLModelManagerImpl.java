package org.protege.editor.owl.model;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.coode.xml.XMLWriterPreferences;
import org.protege.editor.core.AbstractModelManager;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.OWLModelManagerDescriptor;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCache;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCacheImpl;
import org.protege.editor.owl.model.cache.OWLObjectRenderingCache;
import org.protege.editor.owl.model.description.OWLDescriptionParser;
import org.protege.editor.owl.model.description.manchester.ManchesterOWLSyntaxParser;
import org.protege.editor.owl.model.entity.CustomOWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.find.EntityFinder;
import org.protege.editor.owl.model.find.EntityFinderImpl;
import org.protege.editor.owl.model.hierarchy.AssertedClassHierarchyProvider2;
import org.protege.editor.owl.model.hierarchy.OWLDataPropertyHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectPropertyHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.cls.InferredOWLClassHierarchyProvider;
import org.protege.editor.owl.model.history.HistoryManager;
import org.protege.editor.owl.model.history.HistoryManagerImpl;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.OWLReasonerManagerImpl;
import org.protege.editor.owl.model.io.*;
import org.protege.editor.owl.model.library.OntologyLibraryManager;
import org.protege.editor.owl.model.library.folder.FolderOntologyLibrary;
import org.protege.editor.owl.model.repository.OntologyURIExtractor;
import org.protege.editor.owl.model.selection.ontologies.ActiveOntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.AllLoadedOntologiesSelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.ImportsClosureOntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;
import org.protege.editor.owl.model.util.ListenerManager;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.OWLObjectComparator;
import org.protege.editor.owl.ui.clsdescriptioneditor.ManchesterOWLExpressionCheckerFactory;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLExpressionCheckerFactory;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandler;
import org.protege.editor.owl.ui.renderer.*;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owl.util.SimpleURIShortFormProvider;
import org.semanticweb.owl.util.URIShortFormProvider;

import java.io.File;
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

    private URIShortFormProvider uriShortFormProvider;

    private OWLDescriptionParser owlDescriptionParser;

    private boolean includeImports = true;

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

    private OWLObjectHierarchyProvider<OWLClass> assertedClassHierarchyProvider;

    private InferredOWLClassHierarchyProvider inferredClassHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLObjectProperty> toldObjectPropertyHierarchyProvider;

    private OWLObjectHierarchyProvider<OWLDataProperty> toldDataPropertyHierarchyProvider;

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

    private OWLAnnotationURIRenderer annotationRenderer;

    private OWLExpressionCheckerFactory owlExpressionCheckerFactory;


    // error handlers

    private SaveErrorHandler saveErrorHandler;

    private OntologyLoadErrorHandler loadErrorHandler;

    private AutoMappedRepositoryURIMapper autoMappedRepositoryURIMapper;

    private UserResolvedURIMapper userResolvedURIMapper;


    public OWLModelManagerImpl() {
        super();

        modelManagerListenerManager = new ListenerManager<OWLModelManagerListener>();
        changeListenerManager = new ListenerManager<OWLOntologyChangeListener>();
        manager = OWLManager.createOWLOntologyManager();
        manager.setSilentMissingImportsHandling(true);
        manager.addOntologyChangeListener(this);
        manager.addOntologyLoaderListener(this);

        // URI mappers for loading - added in reverse order
        autoMappedRepositoryURIMapper = new AutoMappedRepositoryURIMapper(this);
        userResolvedURIMapper = new UserResolvedURIMapper(new MissingImportHandlerImpl());
        manager.clearURIMappers();
        manager.addURIMapper(userResolvedURIMapper);
        manager.addURIMapper(new WebConnectionURIMapper());
        manager.addURIMapper(new UserRepositoryURIMapper(this));
        manager.addURIMapper(autoMappedRepositoryURIMapper);


        dirtyOntologies = new HashSet<OWLOntology>();
        ontologyRootFolders = new HashSet<File>();
        ontSelectionStrategies = new HashSet<OntologySelectionStrategy>();


        modelManagerChangeListeners = new ArrayList<OWLModelManagerListener>();
        ioListeners = new ArrayList<IOListener>();

        objectRenderer = new OWLObjectRendererImpl(this);
        uriShortFormProvider = new SimpleURIShortFormProvider();
        owlEntityRenderingCache = new OWLEntityRenderingCacheImpl();
        owlEntityRenderingCache.setOWLModelManager(this);
        owlObjectRenderingCache = new OWLObjectRenderingCache(this);

        owlExpressionCheckerFactory = new ManchesterOWLExpressionCheckerFactory(this);

        // @@TODO remove eventually
        owlDescriptionParser = new ManchesterOWLSyntaxParser(this);
        owlDescriptionParser.setOWLModelManager(this);


        activeOntologies = new HashSet<OWLOntology>();

        // force the renderer to be created
        // to prevent double cache rebuild once ontologies loaded
        getOWLEntityRenderer();

        registerOntologySelectionStrategy(new ActiveOntologySelectionStrategy(this));
        registerOntologySelectionStrategy(new AllLoadedOntologiesSelectionStrategy(this));
        registerOntologySelectionStrategy(activeOntologiesStrategy = new ImportsClosureOntologySelectionStrategy(this));

        XMLWriterPreferences.getInstance().setUseNamespaceEntities(XMLWriterPrefs.getInstance().isUseEntities());
    }


    public void dispose() {
        super.dispose();
        
        if (assertedClassHierarchyProvider != null) {
            assertedClassHierarchyProvider.dispose();
        }
        if (inferredClassHierarchyProvider != null) {
            inferredClassHierarchyProvider.dispose();
        }
        if (toldDataPropertyHierarchyProvider != null) {
            toldDataPropertyHierarchyProvider.dispose();
        }
        if (toldObjectPropertyHierarchyProvider != null) {
            toldObjectPropertyHierarchyProvider.dispose();
        }
        try {
            getReasoner().dispose();
        }
        catch (Exception e) {
            logger.error(e.getMessage() + "\n", e);
        }
        // Empty caches
        owlEntityRenderingCache.dispose();
        owlObjectRenderingCache.dispose();

        if (entityRenderer != null){
            entityRenderer.dispose();
        }

        owlReasonerManager.dispose();
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

    ///////////////////////////////////////////////////////////////////////////////////////
    //
    // Loading
    //
    ///////////////////////////////////////////////////////////////////////////////////////

    public void startedLoadingOntology(LoadingStartedEvent event) {
        System.out.println("loading " + event.getOntologyURI() + " from " + event.getPhysicalURI());
        fireBeforeLoadEvent(event.getOntologyURI(), event.getPhysicalURI());
    }


    public void finishedLoadingOntology(LoadingFinishedEvent event) {
        if (!event.isSuccessful()){
            Exception e = event.getException();
            if (loadErrorHandler != null){
                try {
                    loadErrorHandler.handleErrorLoadingOntology(event.getOntologyURI(),
                                                                event.getPhysicalURI(),
                                                                e);
                }
                catch (Throwable e1) {
                    // if, for any reason, the loadErrorHandler cannot report the error
                    ErrorLogPanel.showErrorDialog(e1);
                }
            }
        }
        fireAfterLoadEvent(event.getOntologyURI(), event.getPhysicalURI());
    }


    /**
     * Loads the ontology that has the specified ontology URI.
     * <p/>
     * @param uri The URI of the ontology to be loaded.  Note
     *            that this is <b>not</b> the physical URI of a document
     *            that contains a representation of the ontology.  The
     *            physical location of any concrete representation of the
     *            ontology is determined by the <code>Repository</code>
     *            mechanism.
     */
    public OWLOntology loadOntology(URI uri) throws OWLOntologyCreationException {
        OWLOntology ont = null;
        try{
            ont = manager.loadOntology(uri);
            setActiveOntology(ont);
            fireEvent(EventType.ONTOLOGY_LOADED);
        }
        catch (OWLOntologyCreationException e){
            // will be handled by the loadErrorHandler, so ignore
        }
        return ont;
    }


    public boolean isIncludeImports() {
        return includeImports;
    }


    /**
     * @deprecated use <code>setActiveOntologiesStrategy</code> with either
     * <code>ActiveOntologySelectionStrategy</code> or
     * <code>ImportsClosureOntologySelectionStrategy</code>
     * @param b whether the imports closure of the active ontology should be visible or not
     */
    public void setIncludeImports(boolean b) {
        if (includeImports != b){
            includeImports = b;
            if (includeImports){
                setActiveOntologiesStrategy(new ImportsClosureOntologySelectionStrategy(this));
            }
            else{
                setActiveOntologiesStrategy(new ActiveOntologySelectionStrategy(this));
            }
        }
    }


    /**
     * A convenience method that loads an ontology from a file
     * The location of the file is specified by the URI argument.
     */
    public boolean loadOntologyFromPhysicalURI(URI uri) throws OWLOntologyCreationException {
        OWLOntology ontology = null;

        // Obtain the actual ontology URI.  This is probably the xml:base
        URI ontologyURI = new OntologyURIExtractor(uri).getOntologyURI();

        // if the ontology has already been loaded, we cannot have more than one ont with the same URI
        // in this ontology manager (and therefore workspace)
        if (manager.getOntology(ontologyURI) != null){
            throw new OWLOntologyCreationException("Not loaded." +
                                                   "\nWorkspace already contains ontology: " + ontologyURI +
                                                   ".\nPlease open the ontology in a new frame.");
        }
        else{
            // Set up a mapping from the ontology URI to the physical URI
            manager.addURIMapper(new SimpleURIMapper(ontologyURI, uri));
            if (uri.getScheme()  != null && uri.getScheme().equals("file")) {
                // Load the URIs of other ontologies that are contained in the
                // same folder.
                addRootFolder(uri);
                //loadOntologyURIMap(new File(uri).getParentFile());
            }
            // Delegate to the load method using the URI of the ontology
            ontology = loadOntology(ontologyURI);

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
            autoMappedRepositoryURIMapper.addLibrary(new FolderOntologyLibrary(file.getParentFile()));
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
            OntologyURIExtractor ext = new OntologyURIExtractor(curFile.toURI());
            manager.addURIMapper(new SimpleURIMapper(ext.getOntologyURI(), curFile.toURI()));
            if (logger.isInfoEnabled()) {
                logger.info("Adding auto-mapping: " + ext.getOntologyURI() + " -> " + curFile.toURI());
            }
        }
    }


    public OWLOntology createNewOntology(URI logicalURI, URI physicalURI) throws OWLOntologyCreationException {
        manager.addURIMapper(new SimpleURIMapper(logicalURI, physicalURI));
        OWLOntology ont = manager.createOntology(logicalURI);
        dirtyOntologies.add(ont);
        setActiveOntology(ont);
        fireEvent(EventType.ONTOLOGY_CREATED);
        return ont;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Saving
    //
    ///////////////////////////////////////////////////////////////////////////////////////


    public void save() throws OWLOntologyStorageException {
        // Save all of the ontologies that are editable and that
        // have been modified.
        for (OWLOntology ont : dirtyOntologies) {
            saveOntology(ont);
        }
        dirtyOntologies.clear();
    }


    private void saveOntology(OWLOntology ont) throws OWLOntologyStorageException {
        try{
            fireBeforeSaveEvent(ont.getURI(), manager.getPhysicalURIForOntology(ont));
            manager.saveOntology(ont, manager.getOntologyFormat(ont), manager.getPhysicalURIForOntology(ont));
            fireAfterSaveEvent(ont.getURI(), manager.getPhysicalURIForOntology(ont));
        }
        catch(OWLOntologyStorageException e){
            if (saveErrorHandler != null){
                try {
                    saveErrorHandler.handleErrorSavingOntology(ont, manager.getPhysicalURIForOntology(ont), e);
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


    public void saveAs() throws OWLOntologyStorageException {
        save();
    }


    public Set<OWLOntology> getOntologies() {
        return manager.getOntologies();
    }


    public Set<OWLOntology> getDirtyOntologies() {
        return Collections.unmodifiableSet(dirtyOntologies);
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


    public OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
        if (assertedClassHierarchyProvider == null) {
            assertedClassHierarchyProvider = new AssertedClassHierarchyProvider2(manager);
            assertedClassHierarchyProvider.setOntologies(getActiveOntologies());
        }
        return assertedClassHierarchyProvider;
    }


    public OWLObjectHierarchyProvider<OWLClass> getInferredOWLClassHierarchyProvider() {
        if (inferredClassHierarchyProvider == null) {
            inferredClassHierarchyProvider = new InferredOWLClassHierarchyProvider(this, manager);
        }
        return inferredClassHierarchyProvider;
    }


    public OWLObjectHierarchyProvider<OWLObjectProperty> getOWLObjectPropertyHierarchyProvider() {
        if (toldObjectPropertyHierarchyProvider == null) {
            toldObjectPropertyHierarchyProvider = new OWLObjectPropertyHierarchyProvider(manager);
            toldObjectPropertyHierarchyProvider.setOntologies(getActiveOntologies());
        }
        return toldObjectPropertyHierarchyProvider;
    }


    public OWLObjectHierarchyProvider<OWLDataProperty> getOWLDataPropertyHierarchyProvider() {
        if (toldDataPropertyHierarchyProvider == null) {
            toldDataPropertyHierarchyProvider = new OWLDataPropertyHierarchyProvider(manager);
            toldDataPropertyHierarchyProvider.setOntologies(getActiveOntologies());
        }
        return toldDataPropertyHierarchyProvider;
    }


    public void rebuildOWLClassHierarchy() {
        // Use the accessor methods, as we create the hierarchy lazily
        if (assertedClassHierarchyProvider == null) {
            // Just getting the class hierarchy provider will cause it to be rebuilt
            getOWLClassHierarchyProvider();
        }
        else {
            // Need to get an reset the active ontologies to force a rebuild
            getOWLClassHierarchyProvider().setOntologies(getActiveOntologies());
        }
    }


    public void rebuildOWLObjectPropertyHierarchy() {
        if (toldObjectPropertyHierarchyProvider == null) {
            // Just getting hold of the hierarchy will force a rebuild
            getOWLObjectPropertyHierarchyProvider();
        }
        else {
            getOWLObjectPropertyHierarchyProvider().setOntologies(getActiveOntologies());
        }
    }


    public void rebuildOWLDataPropertyHierarchy() {
        if (toldDataPropertyHierarchyProvider == null) {
            // Just getting hold of the hierarchy will force a rebuild
            getOWLDataPropertyHierarchyProvider();
        }
        else {
            getOWLDataPropertyHierarchyProvider().setOntologies(getActiveOntologies());
        }
    }


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
        logger.info("Setting active ontology to " + activeOntology.getURI());
        rebuildActiveOntologiesCache();
        // Rebuild entity indices
        rebuildEntityIndices();
        // Rebuild the various hierarchies
        if (assertedClassHierarchyProvider != null) {
            rebuildOWLClassHierarchy();
        }
        if (toldObjectPropertyHierarchyProvider != null) {
            rebuildOWLObjectPropertyHierarchy();
        }
        if (toldDataPropertyHierarchyProvider != null) {
            rebuildOWLDataPropertyHierarchy();
        }
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
            manager.applyChange(change);
        }
        catch (OWLOntologyChangeException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public void applyChanges(List<? extends OWLOntologyChange> changes) {
        try {
            manager.applyChanges(changes);
        }
        catch (OWLOntologyChangeException e) {
            throw new OWLRuntimeException(e);
        }
    }


    /**
     * Called when some changes have been applied to various ontologies.  These
     * may be an axiom added or an axiom removed changes.
     * @param changes A list of changes that have occurred.  Each change may be examined
     *                to determine which ontology it was applied to.
     */
    public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
        getHistoryManager().logChanges(changes);
        for (OWLOntologyChange change : changes) {
            dirtyOntologies.add(change.getOntology());
            if (change.isAxiomChange()) {
                OWLAxiom axiom = change.getAxiom();
                if (axiom instanceof OWLImportsDeclaration) {
                    // Reload the active ontology
                    setActiveOntology(getActiveOntology(), true);
                    break;
                }
            }
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

    private void fireBeforeLoadEvent(URI ontologyURI, URI physicalURI) {
        for(IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.beforeLoad(new IOListenerEvent(ontologyURI, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }

    private void fireAfterLoadEvent(URI ontologyURI, URI physicalURI) {
        for(IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.afterLoad(new IOListenerEvent(ontologyURI, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }


    private void fireBeforeSaveEvent(URI ontologyURI, URI physicalURI) {
        for(IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.beforeSave(new IOListenerEvent(ontologyURI, physicalURI));
            }
            catch (Throwable e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
    }


    private void fireAfterSaveEvent(URI ontologyURI, URI physicalURI) {
        for(IOListener listener : new ArrayList<IOListener>(ioListeners)) {
            try {
                listener.afterSave(new IOListenerEvent(ontologyURI, physicalURI));
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
                ren.setup(this);
                ren.initialise();
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
                entityRenderer = new OWLEntityRendererImpl();
            }
        }
        return entityRenderer;
    }


    public String getRendering(OWLObject object) {
        // Look for a cached version of the rending first!
        if (object instanceof OWLEntity) {
            getOWLEntityRenderer();
            String rendering = owlEntityRenderingCache.getRendering((OWLEntity) object);
            if(rendering != null) {
                return rendering;
            }
            else {
                return getOWLEntityRenderer().render((OWLEntity) object);
            }
        }

        return owlObjectRenderingCache.getRendering(object, getOWLObjectRenderer());
    }


    public String getURIRendering(URI uri) {
        if (getOWLEntityRenderer() instanceof OWLEntityAnnotationValueRenderer){
            if (annotationRenderer == null){
                annotationRenderer = new OWLAnnotationURIRenderer(this);
            }
            String shortForm = annotationRenderer.getRendering(uri);
            if (shortForm != null){
                return shortForm;
            }
        }
        return uriShortFormProvider.getShortForm(uri);
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
            entityRenderer.dispose();
        }
        entityRenderer = renderer;
        entityRenderer.addListener(this);
        entityRenderer.initialise();
        rebuildEntityIndices();
        fireEvent(EventType.ENTITY_RENDERER_CHANGED);
    }


    public OWLObjectRenderer getOWLObjectRenderer() {
        return objectRenderer;
    }


    public OWLDescriptionParser getOWLDescriptionParser() {
        return owlDescriptionParser;
    }


    public OWLExpressionCheckerFactory getOWLExpressionCheckerFactory() {
        return owlExpressionCheckerFactory;
    }


    public OWLClass getOWLClass(String rendering) {
        return owlEntityRenderingCache.getOWLClass(rendering);
    }


    public OWLObjectProperty getOWLObjectProperty(String rendering) {
        return owlEntityRenderingCache.getOWLObjectProperty(rendering);
    }


    public OWLDataProperty getOWLDataProperty(String rendering) {
        return owlEntityRenderingCache.getOWLDataProperty(rendering);
    }


    public OWLIndividual getOWLIndividual(String rendering) {
        return owlEntityRenderingCache.getOWLIndividual(rendering);
    }


    public OWLEntity getOWLEntity(String rendering) {
        return owlEntityRenderingCache.getOWLEntity(rendering);
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


    public List<OWLDataType> getMatchingOWLDataTypes(String renderingStart) {
        List<OWLDataType> datatypes = new ArrayList<OWLDataType>();
        for (OWLDataType dt : new OWLDataTypeUtils(getOWLOntologyManager()).getBuiltinDatatypes()) {
            if (getRendering(dt).startsWith(renderingStart)) {
                datatypes.add(dt);
            }
        }
        return datatypes;
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
        annotationRenderer = null;
        logger.info("... rebuilt in " + (System.currentTimeMillis() - t0) + " ms");
    }


    public <T extends OWLObject> Comparator<T> getOWLObjectComparator(){
        return new OWLObjectComparator<T>(this);
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
        userResolvedURIMapper.setMissingImportHandler(missingImportHandler);
    }


    public void setSaveErrorHandler(SaveErrorHandler handler) {
        this.saveErrorHandler = handler;
    }


    public void setLoadErrorHandler(OntologyLoadErrorHandler handler) {
        this.loadErrorHandler = handler;
    }
}
