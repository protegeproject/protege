package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.AbstractSelectOntologiesPage;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;
import java.util.Set;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 11-Sep-2008<br><br>
 */
public class SelectSourceOntologiesPanel extends AbstractSelectOntologiesPage {

    public static final String ID = "SelectSourceOntologiesPanel";

    public SelectSourceOntologiesPanel(OWLEditorKit owlEditorKit) {
        super(ID, owlEditorKit, "Select source ontology");
        setInstructions("Select the ontology that you want to copy/move/delete axioms from");
    }


    public Object getNextPanelDescriptor() {
        return SelectKitPanel.ID;
    }


    public Object getBackPanelDescriptor() {
        return null;
    }


    protected Set<OWLOntology> getDefaultOntologies() {
        return getOWLModelManager().getActiveOntologies();
    }


    public void aboutToHidePanel() {
        super.aboutToHidePanel();
        ((MoveAxiomsWizard) getWizard()).setSourceOntologies(getOntologies());
    }

    protected boolean isMultiSelect() {
        return false;
    }


    public Dimension getPreferredSize() {
        return new Dimension(1200, 800);
    }
}
