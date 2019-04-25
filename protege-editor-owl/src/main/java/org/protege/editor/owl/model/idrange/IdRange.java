package org.protege.editor.owl.model.idrange;

import com.google.auto.value.AutoValue;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-23
 *
 * Represents the numeric range for the numeric part of an OBO Id
 */
@AutoValue
public abstract class IdRange {

    /**
     * Gets the id range for the specified lower and upper bound.
     * @param lowerBound The lower bound
     * @param upperBound The upper bound
     */
    public static IdRange getIdRange(int lowerBound, int upperBound) {
        return new AutoValue_IdRange(lowerBound, upperBound);
    }

    /**
     * Gets the lower (inclusive) bound for the id range
     */
    public abstract int getLowerBound();

    /**
     * Gets the upper (inclusive) bound for the id range
     */
    public abstract int getUpperBound();

    /**
     * Determines whether this range is well formed.  This is true if the
     * lower bound less than or equal to the upper bound and, the lower bound
     * is greater than or equal to zero.
     */
    public boolean isWellFormed() {
        return 0 <= getLowerBound() && getLowerBound() <= getUpperBound();
    }

    /**
     * Determines if the range is empty.  This occurs of the lower bound is
     * equal to the upper bound
     */
    public boolean isEmpty() {
        return getLowerBound() == getUpperBound();
    }
}
