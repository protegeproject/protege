package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.ui.ontology.wizard.AbstractSelectOntologiesPage;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import java.util.Set;
import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
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
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 11-Sep-2008<br><br>
 */
public class SelectSourceOntologiesPanel extends AbstractSelectOntologiesPage {

    public static final String ID = "SelectSourceOntologiesPanel";

    public SelectSourceOntologiesPanel(OWLEditorKit owlEditorKit) {
        super(ID, owlEditorKit, "Select source ontologies");
        setInstructions("Select the ontology that you want to move or copy axioms from");
    }


    public Object getNextPanelDescriptor() {
        return ((MoveAxiomsWizard) getWizard()).getFirstPanelIDForKit();
    }


    public Object getBackPanelDescriptor() {
        return SelectKitPanel.ID;
    }


    protected Set<OWLOntology> getDefaultOntologies() {
        return getOWLModelManager().getActiveOntologies();
    }


    public void aboutToHidePanel() {
        super.aboutToHidePanel();
        ((MoveAxiomsWizard) getWizard()).setSourceOntologies(getOntologies());
    }

    protected boolean isMultiSelect() {
        return false;
    }


    public Dimension getPreferredSize() {
        return new Dimension(1200, 800);
    }
}
