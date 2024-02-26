package org.protege.editor.owl.model.hierarchy.tabbed;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 6 Sep 16
 */
public class CreateHierarchyChangeGenerator<E extends OWLEntity> {

    private final HierarchyNodeCreator<E> hierarchyNodeCreator;

    private final HierarchyAxiomProvider<E> hierarchyAxiomProvider;

    private final OWLOntology targetOntology;



    public CreateHierarchyChangeGenerator(@Nonnull HierarchyNodeCreator<E> hierarchyNodeCreator,
                                          @Nonnull HierarchyAxiomProvider<E> hierarchyAxiomProvider,
                                          @Nonnull OWLOntology targetOntology) {
        this.hierarchyNodeCreator= checkNotNull(hierarchyNodeCreator);
        this.hierarchyAxiomProvider = checkNotNull(hierarchyAxiomProvider);
        this.targetOntology = checkNotNull(targetOntology);
    }

    @Nonnull
    public ImmutableSetMultimap<E, E> generateHierarchy(@Nonnull Collection<Edge> edges, List<OWLOntologyChange> changeList) {
        Multimap<E, E> parentChildMap = HashMultimap.create();
        for (Edge edge : edges) {
            generateChangesForEdge(edge, changeList, parentChildMap);
        }
        return ImmutableSetMultimap.copyOf(parentChildMap);
    }

    private void generateChangesForEdge(@Nonnull Edge edge,
                                        @Nonnull List<OWLOntologyChange> changeList,
                                        @Nonnull Multimap<E, E> parentChildMap) {
        final E parent = generateParent(edge, changeList);
        final E child = generateChild(edge, changeList);
        parentChildMap.put(parent, child);
        Optional<OWLAxiom> parentChildRelationship = hierarchyAxiomProvider.getAxiom(child, parent);
        parentChildRelationship.ifPresent(ax -> changeList.add(new AddAxiom(targetOntology, ax)));
    }

    @Nonnull
    private E generateParent(@Nonnull Edge edge,
                             @Nonnull List<OWLOntologyChange> changeList) {
        return hierarchyNodeCreator.createEntity(edge.getParentName(), changeList);
    }

    @Nonnull
    private E generateChild(@Nonnull Edge edge,
                            @Nonnull List<OWLOntologyChange> changeList) {
        return hierarchyNodeCreator.createEntity(Optional.ofNullable(edge.getChildName()), changeList);
    }
}
