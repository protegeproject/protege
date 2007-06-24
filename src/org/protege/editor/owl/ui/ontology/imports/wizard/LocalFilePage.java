package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Collections;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.protege.editor.core.ui.util.FilePathPanel;
import org.protege.editor.owl.OWLEditorKit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LocalFilePage extends AbstractImportSourcePage {

    public static final String ID = "LocalFilePage";

    private FilePathPanel filePathPanel;


    public LocalFilePage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from local file", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please specify the path to a file that contains an ontology.  You can use the browse " + "button to show a file chooser dialog.");
        filePathPanel = new FilePathPanel("Please select a file", Collections.EMPTY_SET);
        filePathPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                File f = filePathPanel.getFile();
                getWizard().setNextFinishButtonEnabled(f.exists() && f.isDirectory() == false);
            }
        });
        parent.setLayout(new BorderLayout());
        parent.add(filePathPanel, BorderLayout.NORTH);
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return ImportVerificationPage.ID;
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        filePathPanel.requestFocus();
    }


    public ImportVerifier getImportVerifier() {
        return new LocalFileImportVerifier(getOWLEditorKit(), filePathPanel.getFile());
    }
}
