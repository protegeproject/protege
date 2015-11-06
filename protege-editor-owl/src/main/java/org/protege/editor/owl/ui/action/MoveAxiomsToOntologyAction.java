package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsWizard;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class MoveAxiomsToOntologyAction extends ProtegeOWLAction {

    private Logger logger = LoggerFactory.getLogger(MoveAxiomsToOntologyAction.class);

    public void actionPerformed(ActionEvent actionEvent) {
        MoveAxiomsWizard wiz = new MoveAxiomsWizard(getOWLEditorKit());

        if (wiz.showModalDialog() == Wizard.FINISH_RETURN_CODE){
            try {
                wiz.applyChanges();
            }
            catch (OWLOntologyCreationException e) {
                logger.error("An error occurred whilst applying the changes to move axiom to ontology.", e);
            }
        }

        wiz.dispose();
    }


    public void initialise() throws Exception {
    }

    public void dispose() throws Exception {
    }
}
