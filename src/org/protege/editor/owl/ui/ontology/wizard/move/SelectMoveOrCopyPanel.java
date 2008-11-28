package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.core.ui.wizard.WizardPanel;

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
public class SelectMoveOrCopyPanel extends AbstractMoveAxiomsWizardPanel {

    public static final String ID = "SelectMoveOrCopyPanel";

    private JRadioButton moveAxiomsButton;

    private JRadioButton copyAxiomsButton;

    public SelectMoveOrCopyPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Move or copy axioms", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        parent.add(box);
        ButtonGroup bg = new ButtonGroup();
        copyAxiomsButton = new JRadioButton("Copy axioms", true);
        bg.add(copyAxiomsButton);
        box.add(copyAxiomsButton);
        moveAxiomsButton = new JRadioButton("Move axioms");
        bg.add(moveAxiomsButton);
        box.add(moveAxiomsButton);
        setInstructions("Specify whether you want to move the axioms from the source ontology, or copy axioms" +
                "from the source ontology.");
    }


    public Object getBackPanelDescriptor() {
        return NewOntologyPhysicalLocationPanel.ID;
    }


    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }


    public void aboutToDisplayPanel() {
        super.aboutToDisplayPanel();
        
    }


    public void aboutToHidePanel() {
        super.aboutToHidePanel();
        getWizard().setCopyAxioms(copyAxiomsButton.isSelected());
    }
}
