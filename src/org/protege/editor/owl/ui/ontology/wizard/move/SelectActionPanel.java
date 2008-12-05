package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class SelectActionPanel extends AbstractMoveAxiomsWizardPanel {

    public static final String ID = "SelectActionPanel";

    private JRadioButton moveAxiomsButton;

    private JRadioButton copyAxiomsButton;

    private JRadioButton deleteAxiomsButton;

    public SelectActionPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Copy, move or delete axioms", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());

        copyAxiomsButton = new JRadioButton("Copy axioms (to another ontology)", true);
        moveAxiomsButton = new JRadioButton("Move axioms (to another ontology)");
        deleteAxiomsButton = new JRadioButton("Delete axioms");

        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                getWizard().resetButtonStates();
            }
        };
        copyAxiomsButton.addActionListener(actionListener);
        moveAxiomsButton.addActionListener(actionListener);
        deleteAxiomsButton.addActionListener(actionListener);

        ButtonGroup bg = new ButtonGroup();
        bg.add(copyAxiomsButton);
        bg.add(moveAxiomsButton);
        bg.add(deleteAxiomsButton);

        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(copyAxiomsButton);
        box.add(moveAxiomsButton);
        box.add(deleteAxiomsButton);

        parent.add(box);

        setInstructions("Specify whether you want to copy, move or delete the axioms from the source ontology.");
    }


    public Object getBackPanelDescriptor() {
        return getWizard().getLastPanelIDForKit();
    }


    public Object getNextPanelDescriptor() {
        if (isAddToTargetOntology()){
            return SelectTargetOntologyTypePanel.ID;
        }
        return WizardPanel.FINISH;
    }


    public void aboutToDisplayPanel() {
        super.aboutToDisplayPanel();

    }

    private boolean isDeleteFromOriginalOntology() {
        return deleteAxiomsButton.isSelected() || moveAxiomsButton.isSelected();
    }


    private boolean isAddToTargetOntology() {
        return moveAxiomsButton.isSelected() || copyAxiomsButton.isSelected();
    }


    public void aboutToHidePanel() {
        super.aboutToHidePanel();
        getWizard().setDeleteFromOriginalOntology(isDeleteFromOriginalOntology());
        getWizard().setAddToTargetOntology(isAddToTargetOntology());
    }
}
