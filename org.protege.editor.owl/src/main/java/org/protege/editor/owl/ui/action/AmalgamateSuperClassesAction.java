package org.protege.editor.owl.ui.action;


import java.awt.event.ActionEvent;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import org.semanticweb.owlapi.change.AmalgamateSubClassAxioms;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Aug-2007<br><br>
 */
public class AmalgamateSuperClassesAction extends ProtegeOWLAction {


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }


    public void actionPerformed(ActionEvent e) {
        AmalgamateSubClassAxioms change = new AmalgamateSubClassAxioms(getOWLModelManager().getOWLDataFactory(),
                                                                       getOWLModelManager().getActiveOntologies());
        getOWLModelManager().applyChanges(change.getChanges());
    }
}
