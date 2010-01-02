package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.ui.GatherOntologiesPanel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;


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
        OWLOntologyFormat saveAsFormat = panel.getOntologyFormat();
        File saveAsLocation = panel.getSaveLocation();
        for (OWLOntology ont : panel.getOntologiesToSave()) {
            OWLOntologyFormat format = saveAsFormat;
            OWLOntologyManager man = getOWLModelManager().getOWLOntologyManager();
            if (format == null) {
                format = man.getOntologyFormat(ont);
            }
            URI originalPhysicalURI = man.getOntologyDocumentIRI(ont).toURI();
            String originalPath = originalPhysicalURI.getPath();
            if (originalPath == null) {
                originalPath = System.currentTimeMillis() + ".owl";
            }
            File originalFile = new File(originalPath);
            String originalFileName = originalFile.getName();
            File saveAsFile = new File(saveAsLocation, originalFileName);
            try {
                man.saveOntology(ont, format, IRI.create(saveAsFile));
            }
            catch (OWLOntologyStorageException e1) {
                ProtegeApplication.getErrorLog().handleError(Thread.currentThread(), e1);
                errors = true;
            }
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
