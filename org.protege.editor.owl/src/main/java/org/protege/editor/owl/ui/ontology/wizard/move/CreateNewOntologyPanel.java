package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyIDPanel;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 23-Sep-2008<br><br>
 */
public class CreateNewOntologyPanel extends OntologyIDPanel {


    public CreateNewOntologyPanel(OWLEditorKit editorKit) {
        super(editorKit);
    }


    public Object getBackPanelDescriptor() {
        return SelectTargetOntologyTypePanel.ID;
    }


    public void aboutToHidePanel() {
        ((MoveAxiomsWizard) getWizard()).setTargetOntologyID(getOntologyID());
    }
}
