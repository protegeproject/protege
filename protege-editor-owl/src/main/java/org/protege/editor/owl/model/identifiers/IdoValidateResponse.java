package org.protege.editor.owl.model.identifiers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-16
 */
@AutoValue
public abstract class IdoValidateResponse {


    @JsonCreator
    public static IdoValidateResponse get(@Nonnull @JsonProperty("prefix") String prefix,
                               @Nonnull @JsonProperty("identifier") String identifier,
                               @Nonnull @JsonProperty("url") String url) {
        return new AutoValue_IdoValidateResponse(prefix, identifier, url);
    }

    public abstract String getPrefix();

    public abstract String getIdentifier();

    public abstract String getUrl();
}
