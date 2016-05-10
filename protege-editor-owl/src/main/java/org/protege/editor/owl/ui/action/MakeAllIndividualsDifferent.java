package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.refactor.AllDifferentCreator;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.event.ActionEvent;
import java.util.Collections;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MakeAllIndividualsDifferent extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        OWLOntology ont = getOWLModelManager().getActiveOntology();
        AllDifferentCreator creator = new AllDifferentCreator(getOWLModelManager().getOWLDataFactory(), ont, Collections.singleton(ont));
        getOWLModelManager().applyChanges(creator.getChanges());
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
