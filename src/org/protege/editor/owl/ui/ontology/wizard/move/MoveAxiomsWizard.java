package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.axioms.AxiomSelectionStrategy;
import org.protege.editor.owl.ui.ontology.wizard.merge.SelectOntologiesPage;
import org.protege.editor.owl.ui.ontology.wizard.merge.SelectTargetOntologyPage;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.List;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class MoveAxiomsWizard extends Wizard {

    private SelectOntologiesPage sourceOntologiesPage;
    private AxiomSelectionStrategyPanel strategyPanel;
    private StrategyConstrainPanel strategyConstrainPanel;
    private AxiomSelectionPanel axiomsPage;
    private SelectTargetOntologyPage targetOntologyPage;


    public MoveAxiomsWizard(OWLEditorKit eKit) {

        setTitle("Move axioms to ontology");

        sourceOntologiesPage = new SelectOntologiesPage(eKit, "Select source ontologies"){
            public Object getNextPanelDescriptor() {
                return AxiomSelectionStrategyPanel.ID;
            }

            protected Set<OWLOntology> getDefaultOntologies() {
                return getOWLModelManager().getActiveOntologies();
            }

            public void aboutToHidePanel() {
                super.aboutToHidePanel();
                strategyPanel.getSelectionStrategy().setOntologies(getOntologies());
            }
        };
        sourceOntologiesPage.setInstructions("Please select the ontologies you wish to move axioms from.");
        registerWizardPanel(SelectOntologiesPage.ID, sourceOntologiesPage);

        registerWizardPanel(AxiomSelectionStrategyPanel.ID, strategyPanel = new AxiomSelectionStrategyPanel(eKit));
        registerWizardPanel(StrategyConstrainPanel.ID, strategyConstrainPanel = new StrategyConstrainPanel(eKit));
        registerWizardPanel(AxiomSelectionPanel.ID, axiomsPage = new AxiomSelectionPanel(eKit));

        targetOntologyPage = new SelectTargetOntologyPage(eKit, "Select target ontology"){
            public Object getBackPanelDescriptor() {
                return AxiomSelectionPanel.ID;
            }
        };
        targetOntologyPage.setInstructions("Please select a target ontology to move the selected axioms into.");
        registerWizardPanel(SelectTargetOntologyPage.ID, targetOntologyPage);

        setCurrentPanel(SelectOntologiesPage.ID);        
    }

    public Set<OWLOntology> getSourceOntologies() {
        return sourceOntologiesPage.getOntologies();
    }

    public List<OWLAxiom> getAxioms() {
        return axiomsPage.getFilteredAxioms();
    }

    public OWLOntology getTargetOntology() {
        return targetOntologyPage.getOntology();
    }

    public AxiomSelectionStrategy getStrategy() {
        return strategyPanel.getSelectionStrategy();
    }
}
