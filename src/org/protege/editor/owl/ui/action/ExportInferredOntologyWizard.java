package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyURIPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.PhysicalLocationPanel;
import org.semanticweb.owl.util.InferredAxiomGenerator;

import java.net.URI;
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
public class ExportInferredOntologyWizard extends Wizard {

    private ExportInferredOntologyWizardSelectAxiomsPanel axiomsPanel;

    private ExportInferredOntologyURIPanel ontologyURIPanel;

    private PhysicalLocationPanel locationPanel;

    private ExportInferredOntologyIncludeAssertedAxiomsPanel assertedAxiomsPanel;


    public ExportInferredOntologyWizard(OWLEditorKit editorKit) {
        setTitle("Export inferred axioms as ontology");
        registerWizardPanel(ExportInferredOntologyWizardSelectAxiomsPanel.ID,
                            axiomsPanel = new ExportInferredOntologyWizardSelectAxiomsPanel(editorKit));
        setCurrentPanel(ExportInferredOntologyWizardSelectAxiomsPanel.ID);
        registerWizardPanel(ExportInferredOntologyIncludeAssertedAxiomsPanel.ID,
                            assertedAxiomsPanel = new ExportInferredOntologyIncludeAssertedAxiomsPanel(editorKit));
        registerWizardPanel(OntologyURIPanel.ID, ontologyURIPanel = new ExportInferredOntologyURIPanel(editorKit));
        registerWizardPanel(PhysicalLocationPanel.ID, locationPanel = new PhysicalLocationPanel(editorKit));
    }


    public List<InferredAxiomGenerator> getInferredAxiomGenerators() {
        return axiomsPanel.getInferredAxiomGenerators();
    }


    public URI getOntologyURI() {
        return ontologyURIPanel.getURI();
    }


    public URI getPhysicalURI() {
        return locationPanel.getLocationURI();
    }


    public boolean isIncludeAnnotations() {
        return assertedAxiomsPanel.isIncludeAnnotationAxioms();
    }


    public boolean isIncludeAssertedLogicalAxioms() {
        return assertedAxiomsPanel.isIncludeAssertedLogicalAxioms();
    }
}
