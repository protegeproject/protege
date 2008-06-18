package org.protege.editor.owl;

import org.apache.log4j.Logger;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.EditorKitDescriptor;
import org.protege.editor.core.editorkit.EditorKitFactory;
import org.protege.editor.core.editorkit.RecentEditorKitManager;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.SaveErrorHandler;
import org.protege.editor.owl.model.library.OntologyLibraryLoader;
import org.protege.editor.owl.ui.OntologyFormatPanel;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.ontology.imports.missing.MissingImportHandlerUI;
import org.protege.editor.owl.ui.ontology.wizard.create.CreateOntologyWizard;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.io.File;
import java.net.ProtocolException;
import java.net.URI;
import java.util.HashSet;
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
public class OWLEditorKit implements EditorKit {

    private static final Logger logger = Logger.getLogger(OWLEditorKit.class);

    public static final String ID = "OWLEditorKit";

    public static final String URI_KEY = "URI";

    private OWLWorkspace workspace;

    private OWLModelManager modelManager;

    private OWLEditorKitFactory editorKitFactory;

    private Set<URI> newPhysicalURIs;


    public OWLEditorKit(OWLEditorKitFactory editorKitFactory) {
        this.editorKitFactory = editorKitFactory;
        this.newPhysicalURIs = new HashSet<URI>();
        modelManager = new OWLModelManagerImpl();
        loadOntologyLibraries();
        modelManager.setMissingImportHandler(new MissingImportHandlerUI(this));
        modelManager.setSaveErrorHandler(new SaveErrorHandler(){
            public void handleErrorSavingOntology(OWLOntology ont, URI physicalURIForOntology, OWLOntologyStorageException e) throws Exception {
                handleSaveError(ont, physicalURIForOntology, e);
            }
        });
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


    public EditorKitFactory getEditorKitFactory() {
        return editorKitFactory;
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


    private void loadOntologyLibraries() {
        // Attempt to restore any libraries.
        OntologyLibraryLoader loader = new OntologyLibraryLoader(modelManager.getOntologyLibraryManager());
        loader.loadOntologyLibraries();
    }


    private void saveOntologyLibraries() {
        // Attempt to restore any libraries.
        OntologyLibraryLoader loader = new OntologyLibraryLoader(modelManager.getOntologyLibraryManager());
        loader.saveLibraries();
    }


    public OWLModelManager getOWLModelManager() {
        return getModelManager();
    }


    public static final String KEY_PHYSICAL_URI = "KEY_PHYSICAL_URI";


    public boolean handleLoadRecentRequest(EditorKitDescriptor descriptor) throws Exception {
        URI uri = descriptor.getURI(URI_KEY);
        if (uri != null) {
            ((OWLModelManagerImpl) getOWLModelManager()).loadOntologyFromPhysicalURI(uri);
            addRecent(uri);
            return true;
        }
        return false;
    }


    public boolean handleLoadRequest() throws Exception {
        Set<String> ext = new HashSet<String>();
        ext.add("owl");
        ext.add("rdf");
        ext.add("xml");
        ext.add("krss");
        ext.add("obo");
        ext.add("turtle");
        File f = UIUtil.openFile(new JFrame(), "Select an OWL file", ext);
        if (f == null) {
            return false;
        }
        addRecent(f.toURI());
        ((OWLModelManagerImpl) getOWLModelManager()).loadOntologyFromPhysicalURI(f.toURI());
        return true;
    }


    public boolean handleLoadFrom(URI uri) throws Exception {
        addRecent(uri);
        ((OWLModelManagerImpl) getOWLModelManager()).loadOntologyFromPhysicalURI(uri);
        return true;
    }


    public boolean handleNewRequest() throws Exception {
        CreateOntologyWizard w = new CreateOntologyWizard(null, this);
        int result = w.showModalDialog();
        if (result == Wizard.FINISH_RETURN_CODE) {
            URI uri = w.getOntologyURI();
            if (uri != null) {
                getOWLModelManager().createNewOntology(uri, w.getLocationURI());
                newPhysicalURIs.add(w.getLocationURI());
                return true;
            }
        }
        return false;
    }


    public void handleSave() throws Exception {
        try {
            saveOntologyLibraries();
            getOWLModelManager().save();
            getWorkspace().save();
            for (URI uri : newPhysicalURIs) {
                addRecent(uri);
            }
            newPhysicalURIs.clear();
        }
        catch (OWLOntologyStorerNotFoundException e) {
            OWLOntology ont = getOWLModelManager().getActiveOntology();
            OWLOntologyFormat format = getOWLModelManager().getOWLOntologyManager().getOntologyFormat(ont);
            String message = "Could not save ontology in the specified format (" + format + ").\n" + "Please selected 'Save As' and select another format.";
            logger.warn(message);
            JOptionPane.showMessageDialog(getWorkspace(),
                                          message,
                                          "Could not save ontology",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }


    public void handleSaveAs() throws Exception {
        OWLOntologyManager man = getOWLModelManager().getOWLOntologyManager();
        OWLOntology ont = getOWLModelManager().getActiveOntology();
        OWLOntologyFormat format = OntologyFormatPanel.showDialog(this, man.getOntologyFormat(ont));
        if (format == null) {
            logger.warn("Please select a valid format");
            return;
        }
        File file = getSaveAsOWLFile(ont);
        if (file != null){
            man.setOntologyFormat(ont, format);
            man.setPhysicalURIForOntology(ont, file.toURI());
            getOWLModelManager().setDirty(ont);
            handleSave();
        }
        else{
            logger.warn("No valid file specified for the save as operation - quitting");
        }
    }


    public void close() {
        saveOntologyLibraries();
    }

    private void handleSaveError(OWLOntology ont, URI physicalURIForOntology, OWLOntologyStorageException e) throws Exception {
        // catch the case where the user is trying to save an ontology that has been loaded from the web
        if (e.getCause() != null && e.getCause() instanceof ProtocolException){
            handleSaveAs();
        }
    }


    private File getSaveAsOWLFile(OWLOntology ont) {
        UIHelper helper = new UIHelper(this);
        File file = helper.saveOWLFile("Please select a location in which to save: " + getOWLModelManager().getRendering(ont));
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

        private void addRecent(URI physicalURI) {
        String label = physicalURI.toString();
        if (physicalURI.getScheme().equals("file")) {
            label = new File(physicalURI).getPath();
        }
        EditorKitDescriptor descriptor = new EditorKitDescriptor(label, getEditorKitFactory());
        descriptor.setURI(URI_KEY, physicalURI);
        RecentEditorKitManager.getInstance().add(descriptor);
    }
}
