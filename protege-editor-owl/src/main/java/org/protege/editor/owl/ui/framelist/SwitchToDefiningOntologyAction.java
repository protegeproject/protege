package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.event.ActionEvent;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Dec-2007<br><br>
 */
public class SwitchToDefiningOntologyAction<R> extends OWLFrameListPopupMenuAction<R> {

    protected String getName() {
        return "Switch to defining ontology";
    }


    protected void initialise() throws Exception {
    }


    protected void dispose() throws Exception {
    }


    private OWLOntology getSelectedRowOntology() {
        Object selVal = getFrameList().getSelectedValue();
        if (selVal instanceof OWLFrameSectionRow) {
            return ((OWLFrameSectionRow) selVal).getOntology();
        }
        else {
            return null;
        }
    }


    protected void updateState() {
        setEnabled(getSelectedRowOntology() != null);
    }


    public void actionPerformed(ActionEvent e) {
        OWLOntology ont = getSelectedRowOntology();
        if (ont != null) {
            getOWLModelManager().setActiveOntology(ont);
        }
    }
}
