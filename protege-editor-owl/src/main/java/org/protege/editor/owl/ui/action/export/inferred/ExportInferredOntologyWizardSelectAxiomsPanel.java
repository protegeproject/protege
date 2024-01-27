package org.protege.editor.owl.ui.action.export.inferred;

import java.awt.BorderLayout;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-Aug-2007<br><br>
 */
public class ExportInferredOntologyWizardSelectAxiomsPanel extends AbstractOWLWizardPanel {
	private static final long serialVersionUID = 2488132681396978789L;

	public static final String ID = "SELECT_AXIOMS_PANEL";

    private ExportInferredOntologyPanel exportInferredOntologyPanel;


    public ExportInferredOntologyWizardSelectAxiomsPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Select axioms to export", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        JPanel holderPanel = new JPanel(new BorderLayout());
        parent.setLayout(new BorderLayout());
        parent.add(holderPanel);
        holderPanel.add(exportInferredOntologyPanel = new ExportInferredOntologyPanel(), BorderLayout.WEST);
        setInstructions(
                "This wizard will merge inferred and asserted information from ontologies in the imports closure of the active ontology into one ontology." + "Please select the kinds of inferred axioms that you want to export.");
    }


    public List<InferredAxiomGenerator<? extends OWLAxiom>> getInferredAxiomGenerators() {
        return exportInferredOntologyPanel.getInferredAxiomGenerators();
    }


    public Object getNextPanelDescriptor() {
        return ExportInferredOntologyIncludeAssertedAxiomsPanel.ID;
    }
}
