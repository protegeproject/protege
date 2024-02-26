package org.protege.editor.owl.model.deprecation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class DeprecateEntityInfo<E extends OWLEntity> {

    @Nonnull
    private final E entityToDeprecate;

    @Nonnull
    private final String reasonForDeprecation;

    @Nullable
    private final OWLAnnotationValue deprecationCode;

    /**
     * The entity that should be used instead of the deprecated entity.  This is optional.
     */
    @Nullable
    private final E replacementEntity;

    /**
     * A set of alternate entities that should be used instead of the deprecated entity by consumers of
     * the ontology.
     */
    @Nonnull
    private final Set<E> alternateEntities = new HashSet<>();

    public DeprecateEntityInfo(@Nonnull E entityToDeprecate,
                               @Nullable E replacementEntity,
                               @Nonnull String reasonForDeprecation,
                               @Nonnull Set<E> alternateEntities,
                               @Nullable OWLAnnotationValue deprecationCode) {
        this.entityToDeprecate = checkNotNull(entityToDeprecate);
        this.replacementEntity = replacementEntity;
        this.reasonForDeprecation = checkNotNull(reasonForDeprecation);
        this.deprecationCode = deprecationCode;
        this.alternateEntities.addAll(checkNotNull(alternateEntities));
    }

    /**
     * Gets the entity that will be deprecated.
     */
    @Nonnull
    public E getEntityToDeprecate() {
        return entityToDeprecate;
    }

    /**
     * Gets a string that explains the reason for the deprecation.  If non-empty this string will be added as an
     * annotation value to the deprecated entity.
     */
    @Nonnull
    public String getReasonForDeprecation() {
        return reasonForDeprecation;
    }

    @Nonnull
    public Optional<E> getReplacementEntity() {
        return Optional.ofNullable(replacementEntity);
    }

    @Nonnull
    public Set<E> getAlternateEntities() {
        return new HashSet<E>(alternateEntities);
    }

    @Nonnull
    public Optional<OWLAnnotationValue> getDeprecationCode() {
        return Optional.ofNullable(deprecationCode);
    }
}
