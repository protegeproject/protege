package org.protege.editor.owl.ui.action;

import org.protege.editor.core.util.Recommendation;
import org.protege.editor.owl.model.hierarchy.tabbed.CreateHierarchyChangeGenerator;
import org.protege.editor.owl.model.hierarchy.tabbed.HierarchyNodeCreator;
import org.protege.editor.owl.model.hierarchy.tabbed.MakeSiblingsDisjointChangeGenerator;
import org.protege.editor.owl.ui.hierarchy.creation.CreateHierarchyExecutor;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

import java.awt.event.ActionEvent;
import java.util.Optional;

import static org.semanticweb.owlapi.model.EntityType.DATA_PROPERTY;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16 Sep 16
 */
public class CreateSubDataPropertyHierarchyAction extends SelectedOWLDataPropertyAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        OWLDataProperty rootProperty = getOWLWorkspace().getOWLSelectionModel().getLastSelectedDataProperty();
        if(rootProperty == null) {
            return;
        }
        OWLDataFactory df = getOWLDataFactory();
        CreateHierarchyChangeGenerator<OWLDataProperty> hierarchyChangeGenerator = new CreateHierarchyChangeGenerator<>(
                new HierarchyNodeCreator<>(rootProperty,
                        DATA_PROPERTY,
                        getOWLModelManager().getOWLEntityFinder(),
                        getOWLModelManager().getOWLEntityFactory()),
                (child, parent) -> Optional.of(df.getOWLSubDataPropertyOfAxiom(child, parent)),
                getOWLModelManager().getActiveOntology()
        );

        MakeSiblingsDisjointChangeGenerator<OWLDataProperty> disjointsChangeGenerator = new MakeSiblingsDisjointChangeGenerator<>(
                getOWLModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider(),
                (sourceSiblings) -> sourceSiblings,
                (siblings) -> df.getOWLDisjointDataPropertiesAxiom(siblings),
                getOWLModelManager().getActiveOntology()
        );

        CreateHierarchyExecutor<OWLDataProperty> executor = new CreateHierarchyExecutor<>(
                getOWLEditorKit(),
                DATA_PROPERTY,
                Optional.of(Recommendation.NOT_RECOMMENDED),
                hierarchyChangeGenerator,
                disjointsChangeGenerator
        );
        executor.showWizardAndCreateHierarchy();
    }
}
