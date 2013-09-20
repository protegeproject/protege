package org.protege.editor.owl.ui.action;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 03-Feb-2007<br><br>
 */
public class CreateInstanceOfSelectedClassAction extends SelectedOWLClassAction {

    protected void initialiseAction() throws Exception {
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        OWLIndividual ind = getOWLWorkspace().createOWLIndividual().getOWLEntity();
        OWLClass selectedClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        OWLAxiom ax = getOWLDataFactory().getOWLClassAssertionAxiom(selectedClass, ind);
        getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
    }
}
