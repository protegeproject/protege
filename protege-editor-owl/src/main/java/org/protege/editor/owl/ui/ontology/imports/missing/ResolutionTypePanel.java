package org.protege.editor.owl.ui.ontology.imports.missing;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ResolutionTypePanel extends AbstractOWLWizardPanel {

    private JRadioButton specifyFileRadioButton;

    private JRadioButton specifyLibraryButton;

    public static final String ID = "ResolutionTypePanel";


    public ResolutionTypePanel(OWLEditorKit owlEditorKit) {
        super(ID, "Missing import resolution type", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        setInstructions("Please choose what you want to do in order to resolve the missing import");
        ButtonGroup bg = new ButtonGroup();
        Box box = new Box(BoxLayout.Y_AXIS);
        specifyFileRadioButton = new JRadioButton("Specify a file containing the ontology");
        box.add(specifyFileRadioButton);
        bg.add(specifyFileRadioButton);
        box.add(Box.createVerticalStrut(3));
        specifyLibraryButton = new JRadioButton("Add a new ontology library that contains the ontology");
        box.add(specifyLibraryButton);
        bg.add(specifyLibraryButton);
        specifyFileRadioButton.setSelected(true);
        parent.add(box, BorderLayout.NORTH);
    }


    public Object getNextPanelDescriptor() {
        if (specifyFileRadioButton.isSelected()) {
            return SpecifyFilePathPanel.ID;
        }
        else {
            return WizardPanel.FINISH;
        }
    }
}
