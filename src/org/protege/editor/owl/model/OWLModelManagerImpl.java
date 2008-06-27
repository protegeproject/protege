package org.protege.editor.owl.model;

import org.apache.log4j.Logger;
import org.coode.xml.XMLWriterPreferences;
import org.protege.editor.core.AbstractModelManager;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.OWLModelManagerDescriptor;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCache;
import org.protege.editor.owl.model.cache.OWLEntityRenderingCacheImpl;
import org.protege.editor.owl.model.description.OWLDescriptionParser;
import org.protege.editor.owl.model.description.manchester.ManchesterOWLSyntaxParser;
import org.protege.editor.owl.model.entity.LabelledOWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.entity.OWLEntityFactoryImpl;
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
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.protege.editor.owl.model.library.OntologyLibraryManager;
import org.protege.editor.owl.model.library.folder.FolderOntologyLibrary;
import org.protege.editor.owl.model.repository.OntologyURIExtractor;
import org.protege.editor.owl.model.selection.ontologies.ActiveOntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.AllLoadedOntologiesSelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.ImportsClosureOntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;
import org.protege.editor.owl.model.util.ListenerManager;
import org.protege.editor.owl.ui.renderer.*;
import org.semanticweb.owl.apibinding.OWLManager;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.SimpleURIMapper;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;
import org.semanticweb.owl.vocab.XSDVocabulary;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
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
public class OWLModelManagerImpl extends AbstractModelManager implements OWLModelManager, OWLEntityRendererListener, OWLOntologyChangeListener {
    private static final Logger logger = Logger.getLogger(OWLModelManagerImpl.class);

    private List<OWLModelManagerListener> modelManagerChangeListeners;

    private ListenerManager<OWLModelManagerListener> modelManagerListenerManager;

    private ListenerManager<OWLOntologyChangeListener> changeListenerManager;

    private HistoryManager historyManager;

    private OWLModelManagerEntityRenderer entityRenderer;

    private OWLObjectRenderer objectRenderer;

    private OWLDescriptionParser owlDescriptionParser;

    private boolean includeImports = true;

    private OWLOntology activeOntology;

    private MissingImportHandler missingImportHandler;

    private SaveErrorHandler saveErrorHandler;

    private Set<File> ontologyRootFolders;

    private Set<OntologyLibrary> automappedLibraries;

    private OWLEntityRenderingCache owlEntityRenderingCache;

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

    private Map<URI, URI> resolvedMissingImports;

    private OWLEntityFactory defaultOWLEntityFactory;

    private OWLEntityFactory labelOWLEntityFactory;

    /**
     * A cache for the imports closure.  Originally, we just requested this
     * each time from the OWLOntologyManager, but this proved to be expensive
     * in terms of time.
     */
    private Set<OWLOntology> activeOntologies;

    private Set<OntologySelectionStrategy> ontSelectionStrategies = new HashSet<OntologySelectionStrategy>();

    private OntologySelectionStrategy activeOntologiesStrategy;


    public OWLModelManagerImpl() {
        super();

        modelManagerListenerManager = new ListenerManager<OWLModelManagerListener>();
        changeListenerManager = new ListenerManager<OWLOntologyChangeListener>();
        manager = OWLManager.createOWLOntologyManager();
        manager.setSilentMissingImportsHandling(true);
        manager.addOntologyChangeListener(this);
        manager.addURIMapper(new RepositoryURIMapper());

        dirtyOntologies = new HashSet<OWLOntology>();
        ontologyRootFolders = new HashSet<File>();
        automappedLibraries = new HashSet<OntologyLibrary>();

        missingImportHandler = new MissingImportHandlerImpl();
        resolvedMissingImports = new HashMap<URI, URI>();

        modelManagerChangeListeners = new ArrayList<OWLModelManagerListener>();

        objectRenderer = new OWLObjectRendererImpl(this);
        owlDescriptionParser = new ManchesterOWLSyntaxParser(this);
        owlDescriptionParser.setOWLModelManager(this);
        owlEntityRenderingCache = new OWLEntityRenderingCacheImpl();
        owlEntityRenderingCache.setOWLModelManager(this);

        activeOntologies = new HashSet<OWLOntology>();

        registerOntologySelectionStrategy(new ActiveOntologySelectionStrategy(this));
        registerOntologySelectionStrategy(new AllLoadedOntologiesSelectionStrategy(this));
        registerOntologySelectionStrategy(activeOntologiesStrategy = new ImportsClosureOntologySelectionStrategy(this));

        XMLWriterPreferences.getInstance().setUseNamespaceEntities(
                XMLWriterPrefs.getInstance().isUseEntities());
    }


    public void dispose() {
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

        owlReasonerManager.dispose();
        // Name and shame plugins that do not (or can't be bothered to) clean up
        // their listeners!
        modelManagerListenerManager.dumpWarningForAllListeners(System.out,
                                                               "(Listeners should be removed in the plugin dispose method!)");

        changeListenerManager.dumpWarningForAllListeners(System.out,
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
        OWLOntology ont = manager.loadOntology(uri);
        setActiveOntology(ont);
        fireEvent(EventType.ONTOLOGY_LOADED);
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
    public void loadOntologyFromPhysicalURI(URI uri) throws OWLOntologyCreationException {
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
            if (uri.getScheme().equals("file")) {
                // Load the URIs of other ontologies that are contained in the
                // same folder.
                addRootFolder(uri);
                //loadOntologyURIMap(new File(uri).getParentFile());
            }
            // Delegate to the load method using the URI of the ontology
            loadOntology(ontologyURI);

            owlModelManagerDescriptor = new OWLModelManagerDescriptor(uri);
        }
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
            automappedLibraries.add(new FolderOntologyLibrary(file.getParentFile()));
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
            manager.saveOntology(ont, manager.getOntologyFormat(ont), manager.getPhysicalURIForOntology(ont));
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
            catch (Exception e) {
                logger.warn("Exception thrown by listener: " + listener.getClass().getName() + ".  Detatching bad listener!");
                ProtegeApplication.getErrorLog().logError(e);
                modelManagerChangeListeners.remove(listener);
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
                logger.warn("Could not find rendering in cache for: " + object);
                return getOWLEntityRenderer().render((OWLEntity) object);
            }
        }
        return getOWLObjectRenderer().render(object, getOWLEntityRenderer());
    }


    public void renderingChanged(OWLEntity entity, final OWLEntityRenderer renderer) {
        owlEntityRenderingCache.updateRendering(entity);
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
        if (renderer instanceof OWLEntityAnnotationValueRenderer){
            labelOWLEntityFactory = null;
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


    public synchronized OWLEntityFactory getOWLEntityFactory() {
        if (getOWLEntityRenderer() instanceof OWLEntityAnnotationValueRenderer) {
            if (labelOWLEntityFactory == null) {
                labelOWLEntityFactory = new LabelledOWLEntityFactory(this, OWLRDFVocabulary.RDFS_LABEL.getURI(), null){
                    public URI getLabelURI() {
                        final List<URI> uris = OWLRendererPreferences.getInstance().getAnnotationURIs();
                        if (!uris.isEmpty()){
                            return uris.get(0);
                        }
                        return super.getLabelURI();
                    }

                    public String getLabelLanguage() {
                        final List<URI> uris = OWLRendererPreferences.getInstance().getAnnotationURIs();
                        if (!uris.isEmpty()){
                            List<String> langs = OWLRendererPreferences.getInstance().getAnnotationLangs(uris.get(0));
                            if (!langs.isEmpty()){
                                return langs.get(0);
                            }
                        }
                        return super.getLabelLanguage();
                    }
                };
            }
            return labelOWLEntityFactory;
        }
        else {
            if (defaultOWLEntityFactory == null) {
                // Return the default factory
                defaultOWLEntityFactory = new OWLEntityFactoryImpl(this);
            }
            return defaultOWLEntityFactory;
        }
    }


    public void setOWLEntityFactory(OWLEntityFactory owlEntityFactory) {
        this.defaultOWLEntityFactory = owlEntityFactory;
    }


    // The following matching methods are just junk implementations.  They should
    // be replaced with a proper matcher


    public List<OWLClass> getMatchingOWLClasses(String renderingStart) {
        renderingStart = renderingStart.toLowerCase();
        List<OWLClass> entities = new ArrayList<OWLClass>();
        for (String s : owlEntityRenderingCache.getOWLClassRenderings()) {
            if (s.toLowerCase().startsWith(renderingStart)) {
                OWLEntity entity = owlEntityRenderingCache.getOWLClass(s);
                if (entity instanceof OWLClass) {
                    entities.add((OWLClass) entity);
                }
            }
        }
        return entities;
    }


    public List<OWLObjectProperty> getMatchingOWLObjectProperties(String renderingStart) {
        renderingStart = renderingStart.toLowerCase();
        List<OWLObjectProperty> entities = new ArrayList<OWLObjectProperty>();
        for (String s : owlEntityRenderingCache.getOWLObjectPropertyRenderings()) {
            if (s.toLowerCase().startsWith(renderingStart)) {
                OWLEntity entity = owlEntityRenderingCache.getOWLObjectProperty(s);
                if (entity instanceof OWLObjectProperty) {
                    entities.add((OWLObjectProperty) entity);
                }
            }
        }
        return entities;
    }


    public List<OWLDataProperty> getMatchingOWLDataProperties(String renderingStart) {
        renderingStart = renderingStart.toLowerCase();
        List<OWLDataProperty> entities = new ArrayList<OWLDataProperty>();
        for (String s : owlEntityRenderingCache.getOWLDataPropertyRenderings()) {
            if (s.toLowerCase().startsWith(renderingStart)) {
                OWLEntity entity = owlEntityRenderingCache.getOWLDataProperty(s);
                if (entity instanceof OWLDataProperty) {
                    entities.add((OWLDataProperty) entity);
                }
            }
        }
        return entities;
    }


    public List<OWLIndividual> getMatchingOWLIndividuals(String renderingStart) {
        renderingStart = renderingStart.toLowerCase();
        List<OWLIndividual> entities = new ArrayList<OWLIndividual>();
        for (String s : owlEntityRenderingCache.getOWLIndividualRenderings()) {
            if (s.toLowerCase().startsWith(renderingStart)) {
                OWLEntity entity = owlEntityRenderingCache.getOWLIndividual(s);
                if (entity instanceof OWLIndividual) {
                    entities.add((OWLIndividual) entity);
                }
            }
        }
        return entities;
    }


    public List<OWLDataType> getMatchingOWLDataTypes(String renderingStart) {
        List<OWLDataType> datatypes = new ArrayList<OWLDataType>();
        for (URI uri : XSDVocabulary.ALL_DATATYPES) {
            if (uri.getFragment().startsWith(renderingStart)) {
                datatypes.add(manager.getOWLDataFactory().getOWLDataType(uri));
            }
        }
        return datatypes;
    }


    public EntityFinder getEntityFinder() {
        if (entityFinder == null){
            entityFinder = new EntityFinderImpl(owlEntityRenderingCache);
        }
        return entityFinder;
    }


    public void rebuildEntityIndices() {
        logger.info("Rebuilding entity indices...");
        long t0 = System.currentTimeMillis();
        owlEntityRenderingCache.rebuild();
        logger.info("... rebuilt in " + (System.currentTimeMillis() - t0) + " ms");
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

    ////////////////////////////////////////////////////////////////////////////////////////
    //
    // Inner helper classes
    //
    ////////////////////////////////////////////////////////////////////////////////////////


    /**
     * A custom URIMapper.  This is used by the various parsers to
     * convert ontology URIs into physical URIs that point to concrete
     * representations of ontologies.
     * <p/>
     * The mapper uses the following strategy:
     * <p/>
     * 1) It looks in auto-mapped libraries.  These are folder libraries
     * that correspond to folders where the "root ontologies" have been
     * loaded from.  If the mapper finds an ontology that has a mapping
     * to one of these auto-mapped files, then the URI of the
     * auto-mapped file is returned.
     * <p/>
     * 2) It looks in the ontology libraries.  If an ontology library
     * contains an ontology that has the logical URI then the library
     * is asked for the physical URI and this URI is returned.
     * <p/>
     * 3) The system attemps to resolve the logical URI.  If
     * this succeeds then the logical URI is returned.
     * <p/>
     * 4) The system turns to the "Missing Import Handler", which may
     * try to obtain the physical URI (usually by adding a library or
     * by specifying a file etc.)
     */
    private class RepositoryURIMapper implements OWLOntologyURIMapper {


        public URI getPhysicalURI(URI logicalURI) {
            URI uri;
            // Search auto mapped libraries
            Set<OntologyLibrary> libraries = getAutoMappedOntologyLibraries();
            for (OntologyLibrary lib : libraries) {
                if (lib.contains(logicalURI)) {
                    uri = lib.getPhysicalURI(logicalURI);
                    // Map the URI
                    manager.addURIMapper(new SimpleURIMapper(logicalURI, uri));
                    if (logger.isInfoEnabled()) {
                        logger.info("Mapping (from automapping): " + lib.getDescription() + "): " + logicalURI + " -> " + uri);
                    }
                    return uri;
                }
            }

            // Search user defined libraries
            OntologyLibraryManager manager = getOntologyLibraryManager();
            OntologyLibrary lib = manager.getLibrary(logicalURI);
            if (lib != null) {
                uri = lib.getPhysicalURI(logicalURI);
                if (logger.isInfoEnabled()) {
                    logger.info("Mapping (from library: " + lib.getDescription() + "): " + logicalURI + " -> " + uri);
                }
                return lib.getPhysicalURI(logicalURI);
            }
            if (logger.isInfoEnabled()) {
                logger.info("No mapping for " + logicalURI + " found.  Using logical URI");
            }
            // We can't find a local version of the ontology. Can we resolve the URI?
            try {
                // First check that the URI can be resolved.
                URLConnection conn = logicalURI.toURL().openConnection();
                conn.setReadTimeout(5000);
                InputStream is = conn.getInputStream();
                is.close();
                // Opened a stream.  Is it an ontology at the URI?
                OntologyURIExtractor ext = new OntologyURIExtractor(logicalURI);
                ext.getOntologyURI();
                if (ext.isStartElementPresent()) {
                    // There is an ontology at the URI!
                    return logicalURI;
                }
            }
            catch (IOException e) {
                // Can't open the stream - problem resolving the URI
                logger.info(e.getClass().getName() + ": " + e.getMessage());
                // Delegate to the missing imports handler
            }
            // Still haven't managed to resolved the URI
            if (resolvedMissingImports.containsKey(logicalURI)) {
                // Already resolved the missing import - don't ask again
                return resolvedMissingImports.get(logicalURI);
            }
            else {

                URI resolvedURI = resolveMissingImport(logicalURI);
                if (resolvedURI != null) {
                    resolvedMissingImports.put(logicalURI, resolvedURI);
                    return resolvedURI;
                }
            }
            // Final failsafe
            return logicalURI;
        }
    }


    public Set<OntologyLibrary> getAutoMappedOntologyLibraries() {
        return Collections.unmodifiableSet(automappedLibraries);
    }


    public Set<File> getSourceFolders() {
        return Collections.unmodifiableSet(ontologyRootFolders);
    }


    public void setMissingImportHandler(MissingImportHandler missingImportHandler) {
        this.missingImportHandler = missingImportHandler;
    }


    public void setSaveErrorHandler(SaveErrorHandler handler) {
        this.saveErrorHandler = handler;
    }


    private URI resolveMissingImport(URI logicalURI) {
        return missingImportHandler.getPhysicalURI(logicalURI);
    }
}
