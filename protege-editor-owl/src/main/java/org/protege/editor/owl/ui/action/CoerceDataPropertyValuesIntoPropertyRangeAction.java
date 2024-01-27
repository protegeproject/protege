package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import org.semanticweb.owlapi.change.CoerceConstantsIntoDataPropertyRange;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 13-Aug-2007<br><br>
 */
public class CoerceDataPropertyValuesIntoPropertyRangeAction extends ProtegeOWLAction {


    public void actionPerformed(ActionEvent e) {
        CoerceConstantsIntoDataPropertyRange refactor = new CoerceConstantsIntoDataPropertyRange(getOWLDataFactory(),
                                                                                                 getOWLModelManager().getOntologies());

        getOWLModelManager().applyChanges(refactor.getChanges());
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
