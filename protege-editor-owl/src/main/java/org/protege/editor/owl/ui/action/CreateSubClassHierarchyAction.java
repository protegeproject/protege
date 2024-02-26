package org.protege.editor.owl.ui.action;

import static java.util.stream.Collectors.toSet;
import static org.protege.editor.owl.model.util.DefinedClassPredicate.isNotDefinedIn;
import static org.semanticweb.owlapi.model.EntityType.CLASS;

import java.awt.event.ActionEvent;
import java.util.Optional;
import java.util.Set;

import org.protege.editor.core.util.Recommendation;
import org.protege.editor.owl.model.hierarchy.tabbed.CreateHierarchyChangeGenerator;
import org.protege.editor.owl.model.hierarchy.tabbed.HierarchyNodeCreator;
import org.protege.editor.owl.model.hierarchy.tabbed.MakeSiblingsDisjointChangeGenerator;
import org.protege.editor.owl.ui.hierarchy.creation.CreateHierarchyExecutor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 6 Sep 16
 */
public class CreateSubClassHierarchyAction extends SelectedOWLClassAction {

    @Override
    protected void initialiseAction() throws Exception {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OWLClass rootClass = getOWLClass();
        if(rootClass == null) {
            return;
        }
        Set<OWLOntology> activeOntologies = getOWLEditorKit().getOWLModelManager().getActiveOntologies();
        OWLDataFactory df = getOWLDataFactory();

        CreateHierarchyChangeGenerator<OWLClass> hierarchyChangeGenerator = new CreateHierarchyChangeGenerator<>(
                new HierarchyNodeCreator<>(rootClass,
                        CLASS,
                        getOWLModelManager().getOWLEntityFinder(),
                        getOWLModelManager().getOWLEntityFactory()),
                (child, parent) -> parent.isOWLThing() ? Optional.empty() : Optional.of(df.getOWLSubClassOfAxiom(child, parent)),
                getOWLModelManager().getActiveOntology()
        );

        MakeSiblingsDisjointChangeGenerator<OWLClass> disjointsChangeGenerator = new MakeSiblingsDisjointChangeGenerator<>(
                getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider(),
                (sourceSiblings) -> sourceSiblings.stream()
                        .filter(isNotDefinedIn(activeOntologies))
                        .collect(toSet()),
                (siblings) -> df.getOWLDisjointClassesAxiom(siblings),
                getOWLModelManager().getActiveOntology()
        );

        CreateHierarchyExecutor<OWLClass> createClassHierarchyExecutor = new CreateHierarchyExecutor<>(
                getOWLEditorKit(),
                CLASS,
                Optional.of(Recommendation.RECOMMENDED),
                hierarchyChangeGenerator,
                disjointsChangeGenerator
        );
        createClassHierarchyExecutor.showWizardAndCreateHierarchy();
    }
}
