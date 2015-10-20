package org.protege.editor.owl.ui.ontology.wizard.merge;

import org.slf4j.Logger;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.action.OntologyFormatPage;
import org.protege.editor.owl.ui.ontology.wizard.create.OntologyIDPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.PhysicalLocationPanel;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLRuntimeException;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MergeOntologiesWizard extends Wizard {

    private SelectOntologiesPage selectOntologiesPage;

    private OntologyIDPanel IDPanel;

    private PhysicalLocationPanel physicalLocationPanel;

    private OWLModelManager owlModelManager;

    private SelectTargetOntologyPage selectTargetOntologyPage;

    private OntologyFormatPage ontologyFormatPage;


    public MergeOntologiesWizard(OWLEditorKit editorKit) {
        setTitle("Create ontology wizard");
        this.owlModelManager = editorKit.getModelManager();
        registerWizardPanel(SelectOntologiesPage.ID, selectOntologiesPage = new SelectOntologiesPage(editorKit, "Select ontologies to merge"));
        selectOntologiesPage.setInstructions("Please select the ontologies that you want to merge into another ontology.");

        registerWizardPanel(MergeTypePage.ID, new MergeTypePage(editorKit));
        registerWizardPanel(OntologyIDPanel.ID, IDPanel = new OntologyIDPanel(editorKit));
        registerWizardPanel(PhysicalLocationPanel.ID, physicalLocationPanel = new PhysicalLocationPanel(editorKit));
        registerWizardPanel(OntologyFormatPage.ID, ontologyFormatPage = new OntologyFormatPage(editorKit));
        registerWizardPanel(SelectTargetOntologyPage.ID, selectTargetOntologyPage = new SelectTargetOntologyPage(editorKit, "Select ontology to merge into"));
        selectTargetOntologyPage.setInstructions("Please select the target ontology to merge into");

        setCurrentPanel(SelectOntologiesPage.ID);
    }


    public Set<OWLOntology> getOntologiesToMerge() {
        return selectOntologiesPage.getOntologies();
    }


    public OWLOntology getOntologyToMergeInto() {
        OWLOntology ont = selectTargetOntologyPage.getOntology();
        if (ont == null){
            try {
                OWLOntologyID id = IDPanel.getOntologyID();
                ont = owlModelManager.createNewOntology(id, physicalLocationPanel.getLocationURL());
                owlModelManager.getOWLOntologyManager().setOntologyFormat(ont, ontologyFormatPage.getFormat());
            }
            catch (OWLOntologyCreationException e) {
                throw new OWLRuntimeException(e);
            }
        }
        return ont;
    }
}
