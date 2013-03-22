package org.protege.editor.owl.ui.action.export.inferred;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
