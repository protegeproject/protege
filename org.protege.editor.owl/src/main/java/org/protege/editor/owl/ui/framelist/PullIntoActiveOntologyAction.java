package org.protege.editor.owl.ui.framelist;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Dec-2007<br><br>
 */
public class PullIntoActiveOntologyAction<R> extends OWLFrameListPopupMenuAction<R> {

    protected String getName() {
        return "Pull into active ontology";
    }


    protected void initialise() throws Exception {

    }


    protected void dispose() throws Exception {
    }


    protected void updateState() {
        for (OWLFrameSectionRow row : getSelectedRows()) {
            if (row.getOntology() == null || row.getOntology().equals(getOWLEditorKit().getModelManager().getActiveOntology())) {
                setEnabled(false);
                return;
            }
        }
        setEnabled(!getSelectedRows().isEmpty());
    }


    public void actionPerformed(ActionEvent e) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLFrameSectionRow row : getSelectedRows()) {
            OWLAxiom ax = row.getAxiom();
            OWLOntology currentOnt = row.getOntology();
            changes.add(new RemoveAxiom(currentOnt, ax));
            changes.add(new AddAxiom(getOWLEditorKit().getModelManager().getActiveOntology(), ax));
        }
        getOWLModelManager().applyChanges(changes);
    }
}
