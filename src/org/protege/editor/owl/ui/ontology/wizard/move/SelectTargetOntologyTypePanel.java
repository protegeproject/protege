package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyIDPanel;

import javax.swing.*;
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
 * 23-Sep-2008<br><br>
 */
public class SelectTargetOntologyTypePanel extends AbstractMoveAxiomsWizardPanel {


    private JRadioButton mergeIntoExisting;

    private JRadioButton mergeIntoNew;

    public static final String ID = "SelectTargetOntologyTypePanel";


    public SelectTargetOntologyTypePanel(OWLEditorKit owlEditorKit) {
        super(ID, "Target ontology", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Specify whether you want to move/copy the axioms into an existing ontology or a new ontology");
        parent.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        parent.add(box, BorderLayout.NORTH);
        mergeIntoNew = new JRadioButton("New ontology (create a new ontology)", true);
        box.add(mergeIntoNew);
        mergeIntoExisting = new JRadioButton("Existing ontology (choose an existing ontology)");
        box.add(mergeIntoExisting);
        ButtonGroup bg = new ButtonGroup();
        bg.add(mergeIntoNew);
        bg.add(mergeIntoExisting);
    }


    public void displayingPanel() {
        super.displayingPanel();
        mergeIntoNew.requestFocus();
    }


    public Object getBackPanelDescriptor() {
        return SelectActionPanel.ID;
    }


    public Object getNextPanelDescriptor() {
        if (mergeIntoNew.isSelected()) {
            return OntologyIDPanel.ID;
        }
        else {
            return SelectTargetOntologyPanel.ID;
        }
    }

    
}
