package org.protege.editor.owl.model.hierarchy.tabbed;

import com.google.common.collect.*;
import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 6 Sep 16
 */
public class CreateHierarchyChangeGenerator<E extends OWLEntity> {

    private final BiFunction<E, E, OWLAxiom> relationshipProvider;

    private final EntityType<E> entityType;

    private final OWLEntityFactory entityFactory;

    private final E rootEntity;

    private final OWLOntology targetOntology;

    private final OWLEntityFinder entityFinder;

    @SuppressWarnings("unchecked")
    public CreateHierarchyChangeGenerator(@Nonnull E rootEntity,
                                          @Nonnull OWLEntityFinder entityFinder,
                                          @Nonnull OWLEntityFactory entityFactory,
                                          @Nonnull BiFunction<E, E, OWLAxiom> relationshipProvider,
                                          @Nonnull OWLOntology targetOntology) {
        this.rootEntity = checkNotNull(rootEntity);
        this.entityType = (EntityType<E>) checkNotNull(rootEntity.getEntityType());
        this.entityFactory = checkNotNull(entityFactory);
        this.relationshipProvider = checkNotNull(relationshipProvider);
        this.targetOntology = checkNotNull(targetOntology);
        this.entityFinder = checkNotNull(entityFinder);
    }

    @Nonnull
    public CreateHierarchyChanges<E> generateAxioms(@Nonnull Collection<Edge> edges) {
        List<OWLOntologyChange> changeList = new ArrayList<>();
        Multimap<E, E> parentChildMap = HashMultimap.create();
        for (Edge edge : edges) {
            generateChangesForEdge(edge, changeList, parentChildMap);
        }
        return new CreateHierarchyChanges<>(ImmutableList.copyOf(changeList), ImmutableSetMultimap.copyOf(parentChildMap));
    }

    private void generateChangesForEdge(@Nonnull Edge edge,
                                        @Nonnull List<OWLOntologyChange> changeList,
                                        @Nonnull Multimap<E, E> parentChildMap) {
        final E parent = generateParent(edge, changeList);
        final E child = generateChild(edge, changeList);
        parentChildMap.put(parent, child);
        OWLAxiom parentChildRelationship = relationshipProvider.apply(parent, child);
        changeList.add(new AddAxiom(targetOntology, parentChildRelationship));
    }

    @Nonnull
    private E generateParent(@Nonnull Edge edge,
                             @Nonnull List<OWLOntologyChange> changeList) {
        return edge.getParentName()
                .map(parentName -> createEntity(changeList, parentName))
                .orElse(rootEntity);
    }

    @Nonnull
    private E generateChild(@Nonnull Edge edge,
                            @Nonnull List<OWLOntologyChange> changeList) {
        String childName = edge.getChild();
        return createEntity(changeList, childName);
    }

    @Nonnull
    private E createEntity(@Nonnull List<OWLOntologyChange> changeList,
                           @Nonnull String entityName) {
        Optional<E> existingEntity = entityFinder.getOWLEntity(entityType, entityName);
        return existingEntity.orElseGet(() -> generateChangesForFreshEntity(entityName, changeList));
    }

    @Nonnull
    private E generateChangesForFreshEntity(@Nonnull String entityName,
                                            @Nonnull List<OWLOntologyChange> changeList) {
        try {
            OWLEntityCreationSet<E> creationSet = entityFactory.createOWLEntity(entityType, entityName, Optional.<IRI>empty());
            changeList.addAll(creationSet.getOntologyChanges());
            return creationSet.getOWLEntity();
        } catch (OWLEntityCreationException e) {
            throw new RuntimeException(e);
        }
    }

}
