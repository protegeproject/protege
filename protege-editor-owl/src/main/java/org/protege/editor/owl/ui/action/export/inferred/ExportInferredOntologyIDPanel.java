package org.protege.editor.owl.ui.action.export.inferred;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
import javax.swing.JComponent;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyIDPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-Aug-2007<br><br>
 */
public class ExportInferredOntologyIDPanel extends OntologyIDPanel {


    public ExportInferredOntologyIDPanel(OWLEditorKit editorKit) {
        super(editorKit);
    }


    protected void createUI(JComponent parent) {
        super.createUI(parent);
        setInstructions("Please specify the ontology IRI for the inferred ontology");
    }


    public Object getBackPanelDescriptor() {
        return ExportInferredOntologyWizardSelectAxiomsPanel.ID;
    }
}
