package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;



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
        mergeIntoExisting.addActionListener(e -> {
            getWizard().resetButtonStates();
        });
        mergeIntoNew.addActionListener(e -> {
            getWizard().resetButtonStates();
            getWizard().setTargetOntologyID(null);
        });
    }

    @Override
    public void aboutToHidePanel() {
        if(mergeIntoNew.isSelected()) {
            getWizard().setTargetOntologyID(null);
        }
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
            return WizardPanel.FINISH;
        }
        else {
            return SelectTargetOntologyPanel.ID;
        }
    }

    
}
