package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ImportTypePage extends AbstractWizardPanel {

    public static final String ID = "ImportTypePage";

    private final JRadioButton localFileRadioButton = new JRadioButton("Import an ontology contained in a local file.");

    private final JRadioButton webRadioButton = new JRadioButton("Import an ontology contained in a document located on the web.");


    public ImportTypePage(OWLEditorKit owlEditorKit) {
        super(ID, "Import type", owlEditorKit);
        setInstructions("Please choose an option:");
        Box box = new Box(BoxLayout.Y_AXIS);
        box.add(localFileRadioButton);
        box.add(webRadioButton);
        JRadioButton loadedOntologyButton;
        box.add(loadedOntologyButton = new JRadioButton("Import an ontology that is already loaded in the workspace."));
        ButtonGroup bg = new ButtonGroup();
        bg.add(webRadioButton);
        bg.add(localFileRadioButton);
        bg.add(loadedOntologyButton);
        localFileRadioButton.setSelected(true);
        setContent(box);
    }

    public Object getNextPanelDescriptor() {
        if (webRadioButton.isSelected()) {
            return URLPage.ID;
        }
        else if (localFileRadioButton.isSelected()) {
            return LocalFilePage.ID;
        }
        else {
            return LoadedOntologyPage.ID;
        }
    }


    public Object getBackPanelDescriptor() {
        return super.getBackPanelDescriptor();
    }
}
