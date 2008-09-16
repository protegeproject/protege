package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.axioms.*;
import org.protege.editor.owl.ui.ontology.wizard.merge.SelectOntologiesPage;
import org.protege.editor.owl.ui.ontology.wizard.merge.SelectTargetOntologyPage;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * User: nickdrummond Date: May 20, 2008
 */
public class MoveAxiomsWizard extends Wizard {

    private OWLEditorKit editorKit;

    private SelectTargetOntologyPage targetOntologyPage;

    private MoveAxiomsKit selectedKit;

    private Set<OWLOntology> sourceOntologies;

    private OWLOntology targetOntology;

    private List<MoveAxiomsKit> moveAxiomsKits;

    private Set<? extends OWLAxiom> axioms;

    public MoveAxiomsWizard(OWLEditorKit eKit) {
        setTitle("Move axioms to ontology");
        this.editorKit = eKit;

        axioms = new HashSet<OWLAxiom>();
        selectedKit = null;
        setupStrategies();


        registerWizardPanel(SelectOntologiesPage.ID, new SelectSourceOntologiesPage(eKit));
        registerWizardPanel(AxiomSelectionStrategyPanel.ID, new AxiomSelectionStrategyPanel(eKit));
        registerWizardPanel(StrategyConstrainPanel.ID, new StrategyConstrainPanel(eKit));
        registerWizardPanel(AxiomSelectionPanel.ID, new AxiomSelectionPanel(eKit));

        targetOntologyPage = new SelectTargetOntologyPage(eKit, "Select target ontology") {
            public Object getBackPanelDescriptor() {
                return AxiomSelectionPanel.ID;
            }


            public void aboutToHidePanel() {
                setTargetOntology(targetOntologyPage.getOntology());
            }
        };
        targetOntologyPage.setInstructions("Please select a target ontology to move the selected axioms into.");

        registerWizardPanel(SelectTargetOntologyPage.ID, targetOntologyPage);

        setCurrentPanel(SelectOntologiesPage.ID);
    }


    public List<MoveAxiomsKit> getMoveAxiomsKits() {
        return moveAxiomsKits;
    }


    private void setupStrategies() {
        moveAxiomsKits = new ArrayList<MoveAxiomsKit>();

        moveAxiomsKits.add(new MoveAxiomKitImpl(new ClassReferencingAxiomsStrategy()));
        moveAxiomsKits.add(new MoveAxiomKitImpl(new ObjectPropertyReferencingAxiomStrategy()));
        moveAxiomsKits.add(new MoveAxiomKitImpl(new DataPropertyReferencingAxiomStrategy()));
        moveAxiomsKits.add(new MoveAxiomKitImpl(new IndividualReferencingAxiomStrategy()));
        moveAxiomsKits.add(new MoveAxiomKitImpl(new AnnotationAxiomsStrategy()));
        moveAxiomsKits.add(new MoveAxiomKitImpl(new AxiomTypeStrategy()));
        moveAxiomsKits.add(new MoveAxiomKitImpl(new AllAxiomsStrategy()));

        for(MoveAxiomsKit kit : moveAxiomsKits) {
            kit.setup(editorKit);
        }

        MoveAxiomsKitPluginLoader loader = new MoveAxiomsKitPluginLoader(editorKit);
        for(MoveAxiomsKitPlugin plugin : loader.getPlugins()) {
            try {
                MoveAxiomsKit moveAxiomsKit = plugin.newInstance();
                moveAxiomsKits.add(moveAxiomsKit);
            }
            catch (Exception e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }

        for(MoveAxiomsKit kit : moveAxiomsKits) {
            try {
                kit.initialise();
            }
            catch (Exception e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
        selectedKit = moveAxiomsKits.get(0);
    }


    public MoveAxiomsKit getSelectedKit() {
        return selectedKit;
    }


    public void setSelectedKit(MoveAxiomsKit selectedKit) {
        this.selectedKit = selectedKit;
//        selectedKit.getAxiomSelectionStrategy().setOntologies(sourceOntologies);
    }


    public Set<OWLOntology> getSourceOntologies() {
        return sourceOntologies;
    }


    public void setSourceOntologies(Set<OWLOntology> sourceOntologies) {
        this.sourceOntologies = sourceOntologies;
//        if(selectedKit != null) {
//            selectedKit.getAxiomSelectionStrategy().setOntologies(sourceOntologies);
//        }
    }


    public OWLOntology getTargetOntology() {
        return targetOntology;
    }


    public void setTargetOntology(OWLOntology targetOntology) {
        this.targetOntology = targetOntology;
    }


    public Set<? extends OWLAxiom> getAxioms() {
        return axioms;
    }


    public void setAxioms(Set<? extends OWLAxiom> axioms) {
        this.axioms = axioms;
    }
}
