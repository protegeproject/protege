package org.protege.editor.owl.model.idrange;

import javax.annotation.Nonnull;

import com.google.auto.value.AutoValue;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-23
 */
@AutoValue
public abstract class UserIdRange {

    public static UserIdRange get(@Nonnull String userId, @Nonnull IdRange range) {
        return new AutoValue_UserIdRange(userId, range);
    }

    @Nonnull
    public abstract String getUserId();

    @Nonnull
    public abstract IdRange getIdRange();
}
