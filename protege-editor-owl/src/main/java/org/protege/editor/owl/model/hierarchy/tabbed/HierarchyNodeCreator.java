package org.protege.editor.owl.model.hierarchy.tabbed;

import org.protege.editor.owl.model.entity.OWLEntityCreationException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.entity.OWLEntityFactory;
import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 Sep 16
 */
public class HierarchyNodeCreator<E extends OWLEntity> {

    private final E rootEntity;

    private final EntityType<E> entityType;

    private final OWLEntityFinder entityFinder;

    private final OWLEntityFactory entityFactory;

    private final Map<String, OWLEntity> entityName2EntityMap = new HashMap<>();

    /**
     * Constructs a HierarchyNodeCreator that will create hierarchy nodes by reusing existing nodes where possible.
     * @param rootEntity The root node.
     * @param entityType The EntityType of entities in the hierarchy.
     * @param entityFinder An OWLEntityFinder that can be used to look up entities by name to see if they exist.
     *                     If an entity exists it will not be created afresh.
     * @param entityFactory An OWLEntityFactory that can be used to create changes that will generate a new entity
     *                      for a given name.
     */
    public HierarchyNodeCreator(@Nonnull E rootEntity,
                                @Nonnull EntityType<E> entityType,
                                @Nonnull OWLEntityFinder entityFinder,
                                @Nonnull OWLEntityFactory entityFactory) {
        this.rootEntity = checkNotNull(rootEntity);
        this.entityType = checkNotNull(entityType);
        this.entityFinder = checkNotNull(entityFinder);
        this.entityFactory = checkNotNull(entityFactory);
    }

    /**
     * Creates an entity for a node in the hierarchy that has the given name.
     * @param entityName The name.
     * @param changes A list to be populated with changes that are required to create an entity (if necessary).
     * @return The created entity.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public E createEntity(@Nonnull Optional<String> entityName, @Nonnull List<OWLOntologyChange> changes) {
        return entityName.map(name -> {
            Optional<E> existingEntity = entityFinder.getOWLEntity(entityType, name);
            return existingEntity.orElseGet(() -> {
                OWLEntity mappedEntity = entityName2EntityMap.get(name);
                if(mappedEntity != null) {
                    return (E) mappedEntity;
                }
                OWLEntityCreationSet<E> creationSet = createEntity(name);
                changes.addAll(creationSet.getOntologyChanges());
                E freshEntity = creationSet.getOWLEntity();
                entityName2EntityMap.put(name, freshEntity);
                return freshEntity;
            });
        }).orElse(rootEntity);
    }

    private OWLEntityCreationSet<E> createEntity(@Nonnull String name) {
        try {
            return entityFactory.createOWLEntity(entityType, name, Optional.empty());
        } catch (OWLEntityCreationException e) {
            throw new RuntimeException(e);
        }
    }
}
