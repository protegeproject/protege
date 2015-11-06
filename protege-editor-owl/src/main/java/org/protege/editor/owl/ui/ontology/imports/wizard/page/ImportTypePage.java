package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
import java.awt.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportTypePage extends AbstractWizardPanel {

    public static final String ID = "ImportTypePage";

    private JRadioButton webRadioButton;

    private JRadioButton localFileRadioButton;

    private JRadioButton libraryRadioButton;

    private JRadioButton loadedOntologyButton;


    public ImportTypePage(OWLEditorKit owlEditorKit) {
        super(ID, "Import type", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please choose an option:");
        parent.setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(localFileRadioButton = new JRadioButton("Import an ontology contained in a specific file."));
        box.add(webRadioButton = new JRadioButton("Import an ontology contained in a document located on the web."));
        box.add(loadedOntologyButton = new JRadioButton("Import an ontology that is already loaded in the workspace."));
        box.add(libraryRadioButton = new JRadioButton("Import an ontology that is contained in one of the ontology libraries."));
        parent.add(box, BorderLayout.NORTH);
        ButtonGroup bg = new ButtonGroup();
        bg.add(webRadioButton);
        bg.add(localFileRadioButton);
        bg.add(libraryRadioButton);
        bg.add(loadedOntologyButton);
        localFileRadioButton.setSelected(true);
    }


    public Object getNextPanelDescriptor() {
        if (webRadioButton.isSelected()) {
            return URLPage.ID;
        }
        else if (localFileRadioButton.isSelected()) {
            return LocalFilePage.ID;
        }
        else if (libraryRadioButton.isSelected()) {
            return LibraryPage.ID;
        }
        else {
            return LoadedOntologyPage.ID;
        }
    }


    public Object getBackPanelDescriptor() {
        return super.getBackPanelDescriptor();
    }
}
