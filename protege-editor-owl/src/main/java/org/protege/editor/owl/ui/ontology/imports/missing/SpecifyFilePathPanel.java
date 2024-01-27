package org.protege.editor.owl.ui.ontology.imports.missing;

import java.awt.BorderLayout;
import java.util.HashSet;

import javax.swing.JComponent;

import org.protege.editor.core.ui.util.FilePathPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Oct-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SpecifyFilePathPanel extends AbstractOWLWizardPanel {

    public static final String ID = "SpecifyFilePathPanel";

    private FilePathPanel filePathPanel;


    public SpecifyFilePathPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Specify file", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        parent.setLayout(new BorderLayout());
        setInstructions("Please specify the path to the file containing the ontology");
        filePathPanel = new FilePathPanel("Specify ontology file", new HashSet<>());
        parent.add(filePathPanel, BorderLayout.NORTH);
        filePathPanel.addChangeListener(e -> {
            updateState();
        });
    }


    public void displayingPanel() {
        super.displayingPanel();
        filePathPanel.requestFocus();
        updateState();
    }


    private void updateState() {
        getWizard().setNextFinishButtonEnabled(filePathPanel.getFile() != null && filePathPanel.getFile().exists());
    }


    public Object getBackPanelDescriptor() {
        return ResolutionTypePanel.ID;
    }


    public Object getNextPanelDescriptor() {
        return CopyOptionPanel.ID;
    }
}
