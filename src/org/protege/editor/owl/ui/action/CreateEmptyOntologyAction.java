package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.ui.ontology.wizard.create.CreateOntologyWizard;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyCreationException;
import org.semanticweb.owl.model.OWLRuntimeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class CreateEmptyOntologyAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        Frame f = ProtegeManager.getInstance().getFrame(getWorkspace());
        int val = JOptionPane.showConfirmDialog(f,
                                                "This will create an empty ontology in the current set of\n" + "ontologies.  Press OK to continue.",
                                                "Create empty ontology.",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.WARNING_MESSAGE);
        if (val != JOptionPane.OK_OPTION) {
            return;
        }
        CreateOntologyWizard wizard = new CreateOntologyWizard(f, getOWLEditorKit());
        if (wizard.showModalDialog() == Wizard.CANCEL_RETURN_CODE) {
            return;
        }
        URI uri = wizard.getOntologyURI();
        if (uri != null) {
            try {
                OWLOntology ont = getOWLModelManager().createNewOntology(uri, wizard.getLocationURI());
                getOWLModelManager().getOWLOntologyManager().setOntologyFormat(ont, wizard.getFormat());
            }
            catch (OWLOntologyCreationException e1) {
                throw new OWLRuntimeException(e1);
            }
        }
    }


    public void dispose() {
    }


    public void initialise() throws Exception {
    }
}
