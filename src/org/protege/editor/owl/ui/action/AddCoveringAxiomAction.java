package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassExpression;
import org.semanticweb.owl.model.OWLSubClassOfAxiom;

import java.awt.event.ActionEvent;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AddCoveringAxiomAction extends SelectedOWLClassAction {

    private static final Logger logger = Logger.getLogger(AddCoveringAxiomAction.class);

    private OWLSelectionModelListener listener;


    protected void initialiseAction() throws Exception {
    }


    protected void updateState() {
        OWLClass selectedClass = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        setEnabled(selectedClass != null &&
                   getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getChildren(selectedClass).size() > 1);
    }


    public void actionPerformed(ActionEvent e) {
        OWLClass cls = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        // TODO: Push into OWLAPI
        Set<OWLClass> subClses = getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getChildren(cls);
        OWLClassExpression coveringDesc = getOWLDataFactory().getOWLObjectUnionOf(subClses);
        OWLSubClassOfAxiom ax = getOWLDataFactory().getOWLSubClassOfAxiom(cls, coveringDesc);
        getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
    }


    public void dispose() {
        if (listener != null) {
            getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
        }
    }
}
