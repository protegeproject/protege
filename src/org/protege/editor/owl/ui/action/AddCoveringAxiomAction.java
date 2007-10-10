package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLSubClassAxiom;


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


    public void actionPerformed(ActionEvent e) {
        OWLClass cls = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        // TODO: Push into OWLAPI
        Set<OWLClass> subClses = getOWLModelManager().getOWLClassHierarchyProvider().getChildren(cls);
        OWLDescription coveringDesc = getOWLDataFactory().getOWLObjectUnionOf(subClses);
        OWLSubClassAxiom ax = getOWLDataFactory().getOWLSubClassAxiom(cls, coveringDesc);
        getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
    }


    public void dispose() {
        if (listener != null) {
            getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
        }
    }
}
