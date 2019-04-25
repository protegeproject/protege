package org.protege.editor.owl.model.idrange;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-23
 */
@AutoValue
public abstract class IdRangesPolicy {

    public static IdRangesPolicy get(@Nonnull String idPrefix,
                                     int idDigitCount,
                                     @Nonnull String idPolicyFor,
                                     @Nonnull ImmutableList<UserIdRange> userIdRanges) {
        return new AutoValue_IdRangesPolicy(idPrefix, idDigitCount, idPolicyFor, userIdRanges);
    }

    /**
     * Gets the prefix for Ids. The final IRI is this prefix concatenated with an integer
     * in the id range (left padded with "0"s to make this many digits)
     */
    @Nonnull
    public abstract String getIdPrefix();

    /**
     * Gets the number of digits that should form the numeric part of an Id
     */
    public abstract int getIdDigitCount();

    /**
     * Gets the OBO library namespace
     */
    @Nonnull
    public abstract String getIdPolicyFor();

    /**
     * Gets individual user id ranges
     */
    @Nonnull
    public abstract ImmutableList<UserIdRange> getUserIdRanges();

}
