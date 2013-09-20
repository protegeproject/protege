package org.protege.editor.owl.ui.ontology.imports.missing;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class CopyOptionPanel extends AbstractOWLWizardPanel {

    public static final String ID = "CopyOptionPanel";

    private JCheckBox copyCheckBox;


    public CopyOptionPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Copy file to root ontology folder?", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Would you like to copy the file to to root ontology folder? " + "Ontologies are only editable if they are loaded from the same folder that the root importing " + "ontology was loaded from.");

        copyCheckBox = new JCheckBox("Copy to imports root folder", true);
        parent.setLayout(new BorderLayout());
        parent.add(copyCheckBox, BorderLayout.NORTH);
    }


    public Object getBackPanelDescriptor() {
        return SpecifyFilePathPanel.ID;
    }
}
