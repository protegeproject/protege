package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.OWLEditorKit;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 11-Sep-2008<br><br>
 */
public abstract class AbstractMoveAxiomsWizardPanel extends AbstractOWLWizardPanel {


    protected AbstractMoveAxiomsWizardPanel(Object id, String title, OWLEditorKit owlEditorKit) {
        super(id, title, owlEditorKit);
    }


    /**
     * Gets the MoveAxiomsWizard
     */
    public MoveAxiomsWizard getWizard() {
        return (MoveAxiomsWizard) super.getWizard();
    }
}
