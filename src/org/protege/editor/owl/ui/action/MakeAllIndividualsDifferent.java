package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.refactor.AllDifferentCreator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MakeAllIndividualsDifferent extends ProtegeOWLAction {

    private static final Logger logger = Logger.getLogger(MakeAllIndividualsDifferent.class);


    public void actionPerformed(ActionEvent e) {
        AllDifferentCreator creator = new AllDifferentCreator(Collections.singleton(getOWLModelManager().getActiveOntology()));
        getOWLModelManager().applyChanges(creator.getChanges());
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
