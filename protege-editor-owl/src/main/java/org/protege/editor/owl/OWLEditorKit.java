package org.protege.editor.owl;

import com.google.common.base.Optional;
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
import org.protege.editor.core.util.StringAbbreviator;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.io.IOListenerPlugin;
import org.protege.editor.owl.model.io.IOListenerPluginInstance;
import org.protege.editor.owl.model.io.IOListenerPluginLoader;
import org.protege.editor.owl.model.search.*;
import org.protege.editor.owl.ui.OntologyFormatPanel;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.error.OntologyLoadErrorHandlerUI;
import org.protege.editor.owl.ui.explanation.ExplanationManager;
import org.protege.editor.owl.ui.ontology.OntologyPreferences;
import org.protege.editor.owl.ui.ontology.imports.missing.MissingImportHandlerUI;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.VersionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>

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

    private final Set<URI> newPhysicalURIs = new HashSet<>();

    private OntologyLoadErrorHandlerUI loadErrorHandler;

    private ServiceRegistration registration;

    private boolean modifiedDocument = false;

    private OWLOntologyChangeListener ontologyChangeListener;

    private SearchManagerSelector searchManagerSelector;

    public OWLEditorKit(OWLEditorKitFactory editorKitFactory) {
        super(editorKitFactory);
    }


    protected void initialise() {

        logger.info("OWL API Version: {}", VersionInfo.getVersionInfo().getVersion());
        modelManager = new OWLModelManagerImpl();

        modelManager.setExplanationManager(new ExplanationManager(this));
        modelManager.setMissingImportHandler(new MissingImportHandlerUI(this));

        ontologyChangeListener = owlOntologyChanges -> modifiedDocument = true;
        modelManager.addOntologyChangeListener(ontologyChangeListener);

        searchManagerSelector = new SearchManagerSelector(this);

        loadErrorHandler = new OntologyLoadErrorHandlerUI(this);
        modelManager.setLoadErrorHandler(loadErrorHandler);
        loadIOListenerPlugins();
        registration = ProtegeOWL.getBundleContext().registerService(EditorKit.class.getCanonicalName(), this, new Hashtable<>());

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
        String name = customizer.getHeaders().get(Constants.BUNDLE_NAME);
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
        return searchManagerSelector.getCurrentSearchManager();
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
        return new OWLOntologyID(Optional.of(createFreshOntologyIRI()), Optional.<IRI>absent());
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
        owlOntologyManager.setOntologyFormat(ont, new RDFXMLDocumentFormat());
        return true;
    }


    public void handleSave() {
        logger.info(LogBanner.start("Saving Workspace and Ontologies"));
        try {
            OWLOntologyManager ontologyManager = getOWLModelManager().getOWLOntologyManager();
            Set<OWLOntology> ontologiesToSave = new HashSet<>();
            Set<OWLOntology> ontologiesToSaveAs = new HashSet<>();
            OWLOntology activeOntology = modelManager.getActiveOntology();
            IRI activeOntologyDocumentIRI = ontologyManager.getOntologyDocumentIRI(activeOntology);
            if(!"file".equalsIgnoreCase(activeOntologyDocumentIRI.getScheme())) {
                logger.info("Will prompt 'Save As' for the active ontology because it was not loaded from a local file");
                ontologiesToSaveAs.add(activeOntology);
            }
            for(OWLOntology dirtyOntology : getOWLModelManager().getDirtyOntologies()) {
                String ontologyRendering = getModelManager().getRendering(dirtyOntology);
                if(!"file".equals(ontologyManager.getOntologyDocumentIRI(dirtyOntology).getScheme())) {
                    logger.info("Will prompt 'Save As' for the {} ontology because it was not loaded from a local file " +
                            "but has been modified", ontologyRendering);
                    ontologiesToSaveAs.add(dirtyOntology);
                }
                else {
                    logger.info("Will save the {} ontology because it has been modified", ontologyRendering);
                    ontologiesToSave.add(dirtyOntology);
                }
            }
            Map<OWLOntology, OWLOntologyStorageException> saveErrors = new LinkedHashMap<>();
            for(OWLOntology ontology : ontologiesToSaveAs) {
                try {
                    if(!handleSaveAs(ontology)) {
                        // SaveAs aborted.  Abort all?
                        return;
                    }
                } catch (OWLOntologyStorageException e) {
                    saveErrors.put(ontology, e);
                }
            }
            for(OWLOntology ontology : ontologiesToSave) {
                try {
                    getOWLModelManager().save(ontology);
                } catch (OWLOntologyStorageException e) {
                    saveErrors.put(ontology, e);
                }
            }
            getWorkspace().save();
            newPhysicalURIs.forEach(this::addRecent);
            newPhysicalURIs.clear();
            handleSaveErrors(saveErrors);

        } finally {
            logger.info(LogBanner.end());
        }

    }

    private void handleSaveErrors(Map<OWLOntology, OWLOntologyStorageException> saveErrors) {
        if(saveErrors.isEmpty()) {
            return;
        }
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<html><body><b>Some errors where encountered during the save operation.</b><br><br>" +
                "The following ontologies were not saved:<br><br>");
        for(OWLOntology erroredOntology : saveErrors.keySet()) {
            OWLOntologyStorageException error = saveErrors.get(erroredOntology);
            logger.error("An error occurred whilst saving the {} ontology: {}", error.getMessage(), error);
            String rendering = getModelManager().getRendering(erroredOntology);
            errorMessage
                    .append("<b>")
                    .append(rendering)
                    .append("</b><br><span style=\"color:gray;\">Reason: ")
                    .append(StringAbbreviator.abbreviateString(error.getMessage().trim(), 100).replace("\n", "<br>"))
                    .append("</span><br><br>");
        }
        JOptionPane.showMessageDialog(getWorkspace(), errorMessage.toString(), "Save Errors", JOptionPane.ERROR_MESSAGE);
    }



    /**
     * Saves the active ontology
     * @throws Exception
     */
    public void handleSaveAs() {
        final OWLOntology ont = getModelManager().getActiveOntology();
        try {
            handleSaveAs(ont);
        } catch (OWLOntologyStorageException e) {
            Map<OWLOntology, OWLOntologyStorageException> saveErrorMap = new HashMap<>();
            saveErrorMap.put(ont, e);
            handleSaveErrors(saveErrorMap);
        }
    }


    /**
     * Saves the specified ontology to a location that is specified by the user before the save operation.
     * @param ont the ontology to save.
     * @throws OWLOntologyStorageException if there was a problem saving the ontology.
     */
    private boolean handleSaveAs(OWLOntology ont) throws OWLOntologyStorageException {
        OWLOntologyManager man = getModelManager().getOWLOntologyManager();
        OWLDocumentFormat oldFormat = man.getOntologyFormat(ont);
        java.util.Optional<OWLDocumentFormat> format = OntologyFormatPanel.showDialog(this, oldFormat,
                String.format("<html><body>" +
                        "<div>Choose a format to use when saving the <span style='font-weight: bold;'>'%s'</span> ontology.</div>" +
                        "<div style='padding-top: 20px; color: gray'; width: 150px;>" +
                        "(If you are unsure as to what format to choose, " +
                        "we recommend that you use the standard RDF/XML format, " +
                        "or a widely supported format such as Turtle)</div>" +
                        "</body></html>", getModelManager().getRendering(ont)));
        if (!format.isPresent()) {
            logger.info("No ontology document format has been selected.  Aborting saveAs.");
            return false;
        }
        if (oldFormat instanceof PrefixDocumentFormat && format.get() instanceof PrefixDocumentFormat) {
            PrefixDocumentFormat oldPrefixes = (PrefixDocumentFormat) oldFormat;
            for (String name : oldPrefixes.getPrefixNames()) {
                String prefix = oldPrefixes.getPrefix(name);
                if (prefix != null) {
                    ((PrefixDocumentFormat) format.get()).setPrefix(name, prefix);
                }
            }
        }
        File file = getSaveAsOWLFile(ont);
        if (file != null) {
            man.setOntologyFormat(ont, format.get());
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
        File file = helper.saveOWLFile(String.format("Please select a location in which to save: %s", getModelManager().getRendering(ont)));
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
        searchManagerSelector.getCurrentSearchManager().dispose();
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

    public SearchManagerSelector getSearchManagerSelector() {
        return searchManagerSelector;
    }
}
