package org.protege.editor.owl.ui.ontology.wizard.merge;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyIDPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MergeTypePage extends AbstractOWLWizardPanel {

    private JRadioButton mergeIntoExisting;

    private JRadioButton mergeIntoNew;

    public static final String ID = "MergeTypePage";


    public MergeTypePage(OWLEditorKit owlEditorKit) {
        super(ID, "Select merge type", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please select how you would like to merge the ontologies that you have selected. " + "The ontologies can be merged into a brand new ontology, or they can be merged into an existing " + "ontology.");
        parent.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        parent.add(box, BorderLayout.NORTH);
        mergeIntoNew = new JRadioButton("Merge into new ontology", true);
        box.add(mergeIntoNew);
        mergeIntoExisting = new JRadioButton("Merge into existing ontology");
        box.add(mergeIntoExisting);
        ButtonGroup bg = new ButtonGroup();
        bg.add(mergeIntoNew);
        bg.add(mergeIntoExisting);
    }


    public void displayingPanel() {
        super.displayingPanel();
        mergeIntoNew.requestFocus();
    }


    public Object getNextPanelDescriptor() {
        if (mergeIntoNew.isSelected()) {
            return OntologyIDPanel.ID;
        }
        else {
            return SelectTargetOntologyPage.ID;
        }
    }
}
