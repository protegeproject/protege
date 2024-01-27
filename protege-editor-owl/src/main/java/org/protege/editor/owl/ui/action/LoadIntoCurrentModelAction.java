package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JOptionPane;

import org.protege.editor.owl.ui.UIHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LoadIntoCurrentModelAction extends ProtegeOWLAction {

    private Logger logger = LoggerFactory.getLogger(LoadIntoCurrentModelAction.class);

    public void actionPerformed(ActionEvent e) {
        UIHelper helper = new UIHelper(getOWLEditorKit());
        if (helper.showOptionPane("Load ontology?",
                                  "This will open an ontology into the current set of ontologies.\n" + "Do you want to continue?",
                                  JOptionPane.YES_NO_OPTION,
                                  JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            loadOntology();
        }
    }


    private void loadOntology() {
        UIHelper helper = new UIHelper(getOWLEditorKit());
        File file = helper.chooseOWLFile("Select an OWL ontology file");
        if (file == null) {
            return;
        }
        try {
            getOWLEditorKit().handleLoadFrom(file.toURI());
        }
        catch (Exception e) {
            logger.error("An error occurred when loading an ontology from {}", file.getAbsolutePath(), e);
        }
    }


    public void dispose() {
    }


    public void initialise() throws Exception {
    }
}
