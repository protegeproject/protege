package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsWizard;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.awt.event.ActionEvent;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class MoveAxiomsToOntologyAction extends ProtegeOWLAction {

    private Logger logger = Logger.getLogger(MoveAxiomsToOntologyAction.class);

    public void actionPerformed(ActionEvent actionEvent) {
        MoveAxiomsWizard wiz = new MoveAxiomsWizard(getOWLEditorKit());

        if (wiz.showModalDialog() == Wizard.FINISH_RETURN_CODE){
            try {
                wiz.applyChanges();
            }
            catch (OWLOntologyCreationException e) {
                logger.error(e);
            }
        }

        wiz.dispose();
    }


    public void initialise() throws Exception {
    }

    public void dispose() throws Exception {
    }
}
