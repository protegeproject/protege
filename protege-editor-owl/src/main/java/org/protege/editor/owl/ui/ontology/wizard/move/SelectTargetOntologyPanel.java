package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.AbstractSelectOntologiesPage;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 19-Sep-2008<br><br>
 */
public class SelectTargetOntologyPanel extends AbstractSelectOntologiesPage {

    public static final String ID = "SelectTargetOntologyPanel";

    public SelectTargetOntologyPanel(OWLEditorKit owlEditorKit) {
        super(ID, owlEditorKit, "Select target ontology");
    }


    public void aboutToHidePanel() {
        super.aboutToHidePanel();
        ((MoveAxiomsWizard) getWizard()).setTargetOntologyID(getOntologies().iterator().next().getOntologyID());
    }


    public Object getBackPanelDescriptor() {
        return SelectTargetOntologyTypePanel.ID;
    }


    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }
}
