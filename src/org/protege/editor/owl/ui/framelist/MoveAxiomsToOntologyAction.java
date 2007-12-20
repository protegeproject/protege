package org.protege.editor.owl.ui.framelist;

import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.model.*;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
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
public class MoveAxiomsToOntologyAction<R> extends OWLFrameListPopupMenuAction<R> implements OWLOntologyListMenu.OntologySelectedHandler {

    protected String getName() {
        return "Move axiom to ontology...";
    }


    protected void initialise() throws Exception {

    }


    protected void dispose() throws Exception {

    }


    protected void updateState() {
        for (Object val : getFrameList().getSelectedValues()) {
            if (!(val instanceof OWLFrameSectionRow)) {
                setEnabled(false);
                return;
            }
            else {
                if (((OWLFrameSectionRow) val).getOntology() == null) {
                    setEnabled(false);
                    return;
                }
            }
        }
        setEnabled(true);
    }


    public void actionPerformed(ActionEvent e) {
        String title = "Select ontology to move to";
        OWLOntologyListMenu menu = new OWLOntologyListMenu(title,
                                                           getOWLEditorKit(),
                                                           getOWLEditorKit().getOWLModelManager().getOntologies(),
                                                           this);
        menu.show(getFrameList(), getFrameList().getLastMouseDownPoint().x, getFrameList().getLastMouseDownPoint().y);
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
