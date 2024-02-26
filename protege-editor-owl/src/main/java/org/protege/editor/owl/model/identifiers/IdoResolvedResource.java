package org.protege.editor.owl.model.identifiers;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-19
 */
@AutoValue
public abstract class IdoResolvedResource {

    @JsonCreator
    public static IdoResolvedResource get(@Nonnull @JsonProperty("providerCode") String providerCode,
                                      @Nonnull @JsonProperty("description") String description,
                                      @Nonnull @JsonProperty("compactIdentifierResolvedUrl") String compactIdentifierResolvedUrl) {
        return new AutoValue_IdoResolvedResource(providerCode,
                                                 description,
                                                 compactIdentifierResolvedUrl);
    }

    @Nonnull
    public abstract String getProviderCode();

    @Nonnull
    public abstract String getDescription();

    @Nonnull
    public abstract String getCompactIdentifierResolvedUrl();
}
