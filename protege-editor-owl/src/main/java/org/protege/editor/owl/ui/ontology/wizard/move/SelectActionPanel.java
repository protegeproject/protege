package org.protege.editor.owl.ui.ontology.wizard.move;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;


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

        final ActionListener actionListener = event -> getWizard().resetButtonStates();
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
        if (moveAxiomsButton.isSelected() || copyAxiomsButton.isSelected()){
            return SelectTargetOntologyTypePanel.ID;
        }
        return WizardPanel.FINISH;
    }


    public void aboutToDisplayPanel() {
        super.aboutToDisplayPanel();

    }

 
    public void aboutToHidePanel() {
        super.aboutToHidePanel();
 
        if(deleteAxiomsButton.isSelected()) {
            getWizard().setMoveType(MoveType.DELETE);
        }
        else if(moveAxiomsButton.isSelected()) {
            getWizard().setMoveType(MoveType.MOVE);
        }
        else {
            getWizard().setMoveType(MoveType.COPY);
        }
    }
}
