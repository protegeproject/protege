package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.core.util.Recommendation;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.tabbed.*;
import org.protege.editor.owl.ui.hierarchy.creation.CreateHierarchyExecutor;
import org.protege.editor.owl.ui.hierarchy.creation.CreateSubHierarchyWizard;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toSet;
import static org.protege.editor.owl.model.util.DefinedClassPredicate.isDefinedIn;
import static org.protege.editor.owl.model.util.DefinedClassPredicate.isNotDefinedIn;
import static org.semanticweb.owlapi.model.EntityType.CLASS;

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
