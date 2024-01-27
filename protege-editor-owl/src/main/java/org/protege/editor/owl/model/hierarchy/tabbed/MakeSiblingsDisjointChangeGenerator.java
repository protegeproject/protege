package org.protege.editor.owl.model.hierarchy.tabbed;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 19 Sep 16
 */
public class MakeSiblingsDisjointChangeGenerator<E extends OWLEntity> {

    private final OWLObjectHierarchyProvider<E> hierarchyProvider;

    private final Function<Set<E>, Set<E>> siblingsExtractor;

    private final Function<Set<E>, OWLAxiom> disjointSiblingsAxiomFactory;

    private final OWLOntology targetOntology;

    public MakeSiblingsDisjointChangeGenerator(@Nonnull OWLObjectHierarchyProvider<E> existingHierarchyProvider,
                                               @Nonnull Function<Set<E>, Set<E>> disjointSiblingsExtractor,
                                               @Nonnull Function<Set<E>, OWLAxiom> disjointSiblingsAxiomFactory,
                                               @Nonnull OWLOntology targetOntology) {
        this.hierarchyProvider = checkNotNull(existingHierarchyProvider);
        this.siblingsExtractor = checkNotNull(disjointSiblingsExtractor);
        this.disjointSiblingsAxiomFactory = checkNotNull(disjointSiblingsAxiomFactory);
        this.targetOntology = checkNotNull(targetOntology);
    }

    @Nonnull
    public List<OWLOntologyChange> generateChanges(@Nonnull SetMultimap<E, E> parent2ChildMap) {
        checkNotNull(parent2ChildMap);
        List<OWLOntologyChange> changes = new ArrayList<>();
        for(E parent : parent2ChildMap.keySet()) {
            Set<E> children = parent2ChildMap.get(parent);
            Set<E> existingChildren = hierarchyProvider.getChildren(parent);
            Set<E> unionOfChildren = Sets.union(children, existingChildren);
            Set<E> siblingsToMakeDisjoint = siblingsExtractor.apply(unionOfChildren);
            generateChanges(siblingsToMakeDisjoint, changes);
        }
        return changes;
    }

    private void generateChanges(@Nonnull Set<E> siblingsToMakeDisjoint,
                                 @Nonnull List<OWLOntologyChange> changes) {
        if (siblingsToMakeDisjoint.size() < 2) {
            return;
        }
        OWLAxiom disjointSiblingsAxiom = disjointSiblingsAxiomFactory.apply(siblingsToMakeDisjoint);
        changes.add(new AddAxiom(targetOntology, disjointSiblingsAxiom));
    }
}
