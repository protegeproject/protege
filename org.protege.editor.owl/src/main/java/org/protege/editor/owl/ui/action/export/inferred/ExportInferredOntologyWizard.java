package org.protege.editor.owl.ui.action.export.inferred;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.action.OntologyFormatPage;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyIDPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.PhysicalLocationPanel;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;

import java.net.URI;
import java.util.List;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-Aug-2007<br><br>
 */
public class ExportInferredOntologyWizard extends Wizard {

    private ExportInferredOntologyWizardSelectAxiomsPanel axiomsPanel;

    private ExportInferredOntologyIDPanel ontologyIRIPanel;

    private PhysicalLocationPanel locationPanel;

    private ExportInferredOntologyIncludeAssertedAxiomsPanel assertedAxiomsPanel;

    private OntologyFormatPage ontologyFormatPanel;


    public ExportInferredOntologyWizard(OWLEditorKit editorKit) {
        setTitle("Export inferred axioms as ontology");
        registerWizardPanel(ExportInferredOntologyWizardSelectAxiomsPanel.ID,
                            axiomsPanel = new ExportInferredOntologyWizardSelectAxiomsPanel(editorKit));
        setCurrentPanel(ExportInferredOntologyWizardSelectAxiomsPanel.ID);
        registerWizardPanel(ExportInferredOntologyIncludeAssertedAxiomsPanel.ID,
                            assertedAxiomsPanel = new ExportInferredOntologyIncludeAssertedAxiomsPanel(editorKit));
        registerWizardPanel(OntologyIDPanel.ID, ontologyIRIPanel = new ExportInferredOntologyIDPanel(editorKit));
        registerWizardPanel(PhysicalLocationPanel.ID, locationPanel = new PhysicalLocationPanel(editorKit));
        registerWizardPanel(OntologyFormatPage.ID, ontologyFormatPanel = new OntologyFormatPage(editorKit));
    }


    public List<InferredAxiomGenerator<? extends OWLAxiom>> getInferredAxiomGenerators() {
        return axiomsPanel.getInferredAxiomGenerators();
    }


    public OWLOntologyID getOntologyID() {
        return ontologyIRIPanel.getOntologyID();
    }


    public URI getPhysicalURL() {
        return locationPanel.getLocationURL();
    }


    public boolean isIncludeAnnotations() {
        return assertedAxiomsPanel.isIncludeAnnotationAxioms();
    }


    public boolean isIncludeAssertedLogicalAxioms() {
        return assertedAxiomsPanel.isIncludeAssertedLogicalAxioms();
    }


    public OWLOntologyFormat getFormat() {
        return ontologyFormatPanel.getFormat();
    }
}
