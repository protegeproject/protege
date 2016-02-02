package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
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
public class MoveAxiomsToOntologyAction<R> extends OWLFrameListPopupMenuAction<R> {

    protected String getName() {
        return "Move axiom(s) to ontology...";
    }


    protected void initialise() throws Exception {

    }


    protected void dispose() throws Exception {

    }


    protected void updateState() {
        setEnabled(getState());
    }


    private boolean getState() {
        final Object[] sel = getFrameList().getSelectedValues();
        boolean isEnabled = sel.length > 0;
        for (Object val : sel) {
            if (!(val instanceof OWLFrameSectionRow)) {
                return false;
            }
            else {
                if (((OWLFrameSectionRow) val).getOntology() == null) {
                    return false;
                }
            }
        }
        return isEnabled;
    }


    public void actionPerformed(ActionEvent e) {
        OWLOntology ont = new UIHelper(getOWLEditorKit()).pickOWLOntology();
        if (ont != null){
            moveAxiomsToOntology(ont);
        }
    }


    public void ontologySelected(OWLOntology ontology) {
        moveAxiomsToOntology(ontology);
    }


    private void moveAxiomsToOntology(OWLOntology ontology) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLFrameSectionRow row : getSelectedRows()) {
            OWLAxiom ax = row.getAxiom();
            OWLOntology currentOnt = row.getOntology();
            changes.add(new RemoveAxiom(currentOnt, ax));
            changes.add(new AddAxiom(ontology, ax));
        }
        getOWLModelManager().applyChanges(changes);
    }
}
