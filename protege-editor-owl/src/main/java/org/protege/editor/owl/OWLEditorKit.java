package org.protege.editor.owl;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;
import org.protege.editor.core.BookMarkedURIManager;
import org.protege.editor.core.Disposable;
import org.protege.editor.core.editorkit.AbstractEditorKit;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.EditorKitDescriptor;
import org.protege.editor.core.editorkit.RecentEditorKitManager;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.SaveErrorHandler;
import org.protege.editor.owl.model.io.IOListenerPlugin;
import org.protege.editor.owl.model.io.IOListenerPluginInstance;
import org.protege.editor.owl.model.io.IOListenerPluginLoader;
import org.protege.editor.owl.model.search.SearchManager;
import org.protege.editor.owl.model.search.SearchMetadataImportManager;
import org.protege.editor.owl.ui.OntologyFormatPanel;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandlerUI;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.ontology.OntologyPreferences;
import org.protege.editor.owl.ui.ontology.imports.missing.MissingImportHandlerUI;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.VersionInfo;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.ProtocolException;
import java.net.URI;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLEditorKit extends AbstractEditorKit<OWLEditorKitFactory> {

    private static final Logger logger = LoggerFactory.getLogger(OWLEditorKit.class);

    public static final String ID = "OWLEditorKit";

    public static final String URI_KEY = "URI";

    public static final String FILE_URI_SCHEME = "file";

    private OWLWorkspace workspace;

    private OWLModelManager modelManager;

    private Set<URI> newPhysicalURIs;

    private OntologyLoadErrorHandlerUI loadErrorHandler;

    private ServiceRegistration registration;

    private boolean modifiedDocument = false;

    private OWLOntologyChangeListener ontologyChangeListener;

    private SearchManager searchManager;

    public OWLEditorKit(OWLEditorKitFactory editorKitFactory) {
        super(editorKitFactory);
    }


    protected void initialise() {
        logger.info("OWL API Version: {}", VersionInfo.getVersionInfo().getVersion());
        this.newPhysicalURIs = new HashSet<URI>();
        modelManager = new OWLModelManagerImpl();

        modelManager.setExplanationManager(new ExplanationManager(this));
        modelManager.setMissingImportHandler(new MissingImportHandlerUI(this));
        modelManager.setSaveErrorHandler(new SaveErrorHandler() {
            public void handleErrorSavingOntology(OWLOntology ont, URI physicalURIForOntology, OWLOntologyStorageException e) throws Exception {
                handleSaveError(ont, physicalURIForOntology, e);
            }
        });

        ontologyChangeListener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> owlOntologyChanges) throws OWLException {
                modifiedDocument = true;
            }
        };
        modelManager.addOntologyChangeListener(ontologyChangeListener);

        searchManager = new SearchManager(this, new SearchMetadataImportManager());
        loadErrorHandler = new OntologyLoadErrorHandlerUI(this);
        modelManager.setLoadErrorHandler(loadErrorHandler);
        loadIOListenerPlugins();
        registration = ProtegeOWL.getBundleContext().registerService(EditorKit.class.getCanonicalName(), this, new Hashtable<String, Object>());

        getWorkspace().refreshComponents();
    }

    /**
     * Determines if this editor kit has modified the contents if its documents in any way.
     * @return <code>true</code> if this editor kit has modified the contents of its document, otherwise
     *         <code>false</code>.
     */
    public boolean hasModifiedDocument() {
        return modifiedDocument;
    }

    /**
     * @deprecated This call isn't really deprecated - it is just not recommended.  If you are thinking of using this
     *             call
     *             then probably there is a missing feature in Prot&#x00E9g&#x00E9 or there is a plugin capability that
     *             you should consider using instead.  Contact the Prot&#x00E9g&#x00E9 developers on the p4 mailing
     *             list:
     *             http://mailman.stanford.edu/mailman/listinfo/p4-feedback.
     */
    @Deprecated
    public void setOWLModelManager(OWLModelManager modelManager) {
        this.modelManager = modelManager;
        ServiceReference sr = ProtegeOWL.getBundleContext().getServiceReference(PackageAdmin.class.getCanonicalName());
        PackageAdmin admin = (PackageAdmin) ProtegeOWL.getBundleContext().getService(sr);
        Bundle customizer = admin.getBundle(modelManager.getClass());
        String name = (String) customizer.getHeaders().get(Constants.BUNDLE_NAME);
        if (name == null) {
            name = customizer.getSymbolicName();
        }
        getOWLWorkspace().setCustomizedBy("Prot\u00E9g\u00E9 Customized by " + name);
    }

    /**
     * Gets the <code>EditorKit</code> Id.  This can be used to identify
     * the type of <code>EditorKit</code>.
     * @return A <code>String</code> that represents the <code>EditorKit</code>
     *         Id.
     */
    public String getId() {
        return ID;
    }


    /**
     * Gets the <code>Workspace</code> that is used in the UI to
     * display the contents of the clsdescriptioneditor kit "model".
     */
    public OWLWorkspace getWorkspace() {
        if (workspace == null) {
            workspace = new OWLWorkspace();
            workspace.setup(this);
            workspace.initialise();
        }
        return workspace;
    }


    public OWLWorkspace getOWLWorkspace() {
        return getWorkspace();
    }


    /**
     * Gets the "model" that the clsdescriptioneditor kit edits.  This will
     * probably contain one or more ontologies.
     */
    public OWLModelManager getModelManager() {
        return modelManager;
    }


    public OWLModelManager getOWLModelManager() {
        return getModelManager();
    }

    public SearchManager getSearchManager() {
        return searchManager;
    }

    public boolean handleLoadRecentRequest(EditorKitDescriptor descriptor) throws Exception {
        URI uri = descriptor.getURI(URI_KEY);
        return uri != null && handleLoadFrom(uri);
    }


    public boolean handleLoadRequest() throws Exception {
        File f = new UIHelper(this).chooseOWLFile("Select an OWL file");
        return f != null && handleLoadFrom(f.toURI());
    }


    public boolean handleLoadFrom(URI uri) throws Exception {
        loadErrorHandler.setReloadFlag(false);
        boolean success = ((OWLModelManagerImpl) getModelManager()).loadOntologyFromPhysicalURI(uri);

        if (success) {
            addRecent(uri);
        }
        else if (loadErrorHandler.getReloadFlag()) {
            success = handleLoadFrom(uri);
        }
        modifiedDocument = true;
        return success;
    }

    /**
     * Creates an ontology Id which should be used by default.  The current implementation returns the next anonymous
     * ontology identifier.
     * @return The id.
     */
    private OWLOntologyID createDefaultOntologyId() {
        return new OWLOntologyID(createFreshOntologyIRI());
    }

    /**
     * Creates a fresh ontology IRI.
     * @return The ontology IRI.
     */
    private IRI createFreshOntologyIRI() {
        OntologyPreferences ontologyPreferences = OntologyPreferences.getInstance();
        return IRI.create(ontologyPreferences.generateNextURI());
    }

    public boolean handleNewRequest() throws Exception {
        OWLOntologyID id = createDefaultOntologyId();
        OWLOntology ont = getModelManager().createNewOntology(id, URI.create(id.getDefaultDocumentIRI().get().toString()));
        OWLOntologyManager owlOntologyManager = getModelManager().getOWLOntologyManager();
        owlOntologyManager.setOntologyFormat(ont, new RDFXMLOntologyFormat());
        return true;
    }


    public void handleSave() throws Exception {
        try {
            logger.info(LogBanner.start("Saving Workspace and Ontologies"));
            Set<OWLOntology> dirtyOntologies = getModelManager().getDirtyOntologies();
            getWorkspace().save();
            if (dirtyOntologies.isEmpty()) {
                logger.info("No ontology changes detected.  Not writing any ontology documents.");
                return;
            }
            try {
                getModelManager().save();
                for (URI uri : newPhysicalURIs) {
                    addRecent(uri);
                }
                newPhysicalURIs.clear();
                logger.info("Saved ontologies");
            }
            catch (OWLOntologyStorageException e) {
                OWLOntology ont = getModelManager().getActiveOntology();
                OWLDocumentFormat format = getModelManager().getOWLOntologyManager().getOntologyFormat(ont);
                String message = "Could not save ontology in the specified format (" + format + ").\n" + "Please select 'Save As' and choose another format.";
                logger.warn(message);
                ErrorLogPanel.showErrorDialog(new OWLOntologyStorageException(message, e));
            }
        } finally {
            logger.info(LogBanner.end());
        }

    }


    public void handleSaveAs() throws Exception {
        final OWLOntology ont = getModelManager().getActiveOntology();
        if (handleSaveAs(ont)) {
        }
    }


    /**
     * This should only save the specified ontology
     * @param ont the ontology to save
     * @throws Exception
     */
    private boolean handleSaveAs(OWLOntology ont) throws Exception {
        OWLOntologyManager man = getModelManager().getOWLOntologyManager();
        OWLDocumentFormat oldFormat = man.getOntologyFormat(ont);
        OWLDocumentFormat format = OntologyFormatPanel.showDialog(this, oldFormat, "Choose a format to use when saving the " + getModelManager().getRendering(ont) + " ontology");
        if (format == null) {
            logger.warn("Please select a valid format");
            return false;
        }
        if (oldFormat instanceof PrefixOWLOntologyFormat && format instanceof PrefixOWLOntologyFormat) {
            PrefixOWLOntologyFormat oldPrefixes = (PrefixOWLOntologyFormat) oldFormat;
            for (String name : oldPrefixes.getPrefixNames()) {
                ((PrefixOWLOntologyFormat) format).setPrefix(name, oldPrefixes.getPrefix(name));
            }
        }
        File file = getSaveAsOWLFile(ont);
        if (file != null) {
            man.setOntologyFormat(ont, format);
            man.setOntologyDocumentIRI(ont, IRI.create(file));
            getModelManager().save(ont);
            addRecent(file.toURI());
            return true;
        }
        else {
            return false;
        }
    }


    private File getSaveAsOWLFile(OWLOntology ont) {
        UIHelper helper = new UIHelper(this);
        File file = helper.saveOWLFile("Please select a location in which to save: " + getModelManager().getRendering(ont));
        if (file != null) {
            int extensionIndex = file.toString().lastIndexOf('.');
            if (extensionIndex == -1) {
                file = new File(file.toString() + ".owl");
            }
            else if (extensionIndex != file.toString().length() - 4) {
                file = new File(file.toString() + ".owl");
            }
        }
        return file;
    }


    public void addRecent(URI physicalURI) {
        String label = physicalURI.toString();
        if (FILE_URI_SCHEME.equals(physicalURI.getScheme())) {
            label = new File(physicalURI).getPath();
        }
        else {
            // also add to the URI bookmarks
            BookMarkedURIManager.getInstance().add(physicalURI);
        }
        EditorKitDescriptor descriptor = new EditorKitDescriptor(label, getEditorKitFactory());
        descriptor.setURI(URI_KEY, physicalURI);
        RecentEditorKitManager.getInstance().add(descriptor);
    }


    private void handleSaveError(OWLOntology ont, URI physicalURIForOntology, OWLOntologyStorageException e) throws Exception {
        // catch the case where the user is trying to save an ontology that has been loaded from the web
        if (e.getCause() != null && e.getCause() instanceof ProtocolException) {
            handleSaveAs(ont);
        }
        else {
            throw e;
        }
    }

    private void loadIOListenerPlugins() {
        IOListenerPluginLoader loader = new IOListenerPluginLoader(this);
        for (IOListenerPlugin pl : loader.getPlugins()) {
            try {
                IOListenerPluginInstance instance = pl.newInstance();
                getModelManager().addIOListener(instance);
            }
            catch (Throwable e) {
                logger.warn("An IOListenerPlugin threw an error: {}", e);
            }
        }
    }

    /*
    * Call the model manager get and put here because otherwise
    * listeners created by owl editor kit objects may get disposed
    * too late.
    *
    * Feels risky.
    */
    @Override
    public Disposable get(Object key) {
        return getOWLModelManager().get(key);
    }

    @Override
    public void put(Object key, Disposable value) {
        getOWLModelManager().put(key, value);
    }


    @Override
    public void dispose() {
        logger.info(LogBanner.start("Disposing of Workspace"));
        getModelManager().removeOntologyChangeListener(ontologyChangeListener);
        super.dispose();
        searchManager.dispose();
        workspace.dispose();
        try {
            modelManager.dispose();
        }
        catch (Exception e) {
            ErrorLogPanel.showErrorDialog(e);
        }

        if (registration != null) {
            registration.unregister();
            registration = null;
        }
        logger.info(LogBanner.end());
    }
}
