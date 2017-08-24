package org.protege.editor.owl.model.deprecation;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class DeprecateEntityInfo<E extends OWLEntity> {

    /**
     * The entity that will be deprecated.
     */
    @Nonnull
    private final E entityToDeprecate;

    /**
     * A string that explains the reason for the deprecation.  If non-empty this string will be added as an
     * annotation value to the deprecated entity.
     */
    @Nonnull
    private final String reasonForDeprecation;

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
                               @Nonnull String reasonForDeprecation) {
        this.entityToDeprecate = checkNotNull(entityToDeprecate);
        this.replacementEntity = replacementEntity;
        this.reasonForDeprecation = checkNotNull(reasonForDeprecation);
    }

    @Nonnull
    public E getEntityToDeprecate() {
        return entityToDeprecate;
    }

    @Nonnull
    public Optional<E> getReplacementEntity() {
        return Optional.ofNullable(replacementEntity);
    }

    @Nonnull
    public Set<E> getAlternateEntities() {
        return alternateEntities;
    }

    @Nonnull
    public String getReasonForDeprecation() {
        return reasonForDeprecation;
    }
}
