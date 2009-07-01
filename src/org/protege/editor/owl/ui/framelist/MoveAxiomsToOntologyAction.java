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
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLFrameSectionRow row : getSelectedRows()) {
            OWLAxiom ax = row.getAxiom();
            OWLOntology currentOnt = row.getOntology();
            changes.add(new RemoveAxiom(currentOnt, ax));
            changes.add(new AddAxiom(ontology, ax));
        }
        getOWLModelManager().applyChanges(changes);
    }
}
