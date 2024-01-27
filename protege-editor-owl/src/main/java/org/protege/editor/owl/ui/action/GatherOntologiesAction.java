package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.protege.editor.owl.model.io.OntologySaver;
import org.protege.editor.owl.ui.GatherOntologiesPanel;
import org.semanticweb.owlapi.formats.RDFXMLDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-May-2007<br><br>
 */
public class GatherOntologiesAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        // Need to pop a dialog asking where to save
        GatherOntologiesPanel panel = GatherOntologiesPanel.showDialog(getOWLEditorKit());
        if (panel == null) {
            return;
        }
        boolean errors = false;
        OWLDocumentFormat saveAsFormat = panel.getOntologyFormat();
        File saveAsLocation = panel.getSaveLocation();
        OntologySaver.Builder ontologySaverBuilder = OntologySaver.builder();
        for (OWLOntology ont : panel.getOntologiesToSave()) {
            final OWLDocumentFormat format;
            OWLOntologyManager man = getOWLModelManager().getOWLOntologyManager();
            if(saveAsFormat != null) {
                format = saveAsFormat;
            }
            else {
                OWLDocumentFormat documentFormat = man.getOntologyFormat(ont);
                if(documentFormat != null) {
                    format = documentFormat;
                }
                else {
                    format = new RDFXMLDocumentFormat();
                }
            }

            URI originalPhysicalURI = man.getOntologyDocumentIRI(ont).toURI();
            String originalPath = originalPhysicalURI.getPath();
            if (originalPath == null) {
                originalPath = UUID.randomUUID().toString() + ".owl";
            }
            File originalFile = new File(originalPath);
            String originalFileName = originalFile.getName();
            File saveAsFile = new File(saveAsLocation, originalFileName);

            ontologySaverBuilder.addOntology(ont, format, IRI.create(saveAsFile));
        }
        try {
            ontologySaverBuilder.build().saveOntologies();
        }
        catch (OWLOntologyStorageException e1) {
            LoggerFactory.getLogger(GatherOntologiesAction.class)
                    .error("An error occurred whilst saving a gathered ontology: {}", e1);
            errors = true;
        }

        if (errors) {
            JOptionPane.showMessageDialog(getWorkspace(),
                                          "There were errors when saving the ontologies.  Please check the log for details.",
                                          "Error during save",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
