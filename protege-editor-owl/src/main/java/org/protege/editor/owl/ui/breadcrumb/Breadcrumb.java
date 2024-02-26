package org.protege.editor.owl.ui.breadcrumb;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLObject;

import com.google.common.base.Objects;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 *
 * Represents an element of a breadcrumb trail.
 */
public class Breadcrumb {

    @Nonnull
    private final OWLObject object;

    @Nullable
    private final Object parentRelationship;

    /**
     * Constructs a breadcrumb.
     * @param object The object that the breadcrumb represents.
     * @param parentRelationship The relationship to the previous breadcrumb in the trail.
     */
    public Breadcrumb(@Nonnull OWLObject object, @Nullable Object parentRelationship) {
        this.object = checkNotNull(object);
        this.parentRelationship = parentRelationship;
    }

    @Nonnull
    public OWLObject getObject() {
        return object;
    }

    @Nonnull
    public Optional<?> getParentRelationship() {
        return Optional.ofNullable(parentRelationship);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(object, parentRelationship);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Breadcrumb)) {
            return false;
        }
        Breadcrumb other = (Breadcrumb) obj;
        return this.object.equals(other.object)
                && Objects.equal(this.parentRelationship, other.parentRelationship);
    }


    @Override
    public String toString() {
        return toStringHelper("Breadcrumb")
                .addValue(object)
                .toString();
    }
}
