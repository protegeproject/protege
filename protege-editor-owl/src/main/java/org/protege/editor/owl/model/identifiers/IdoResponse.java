package org.protege.editor.owl.model.identifiers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-19
 */
@AutoValue
public abstract class IdoResponse {

    @JsonCreator
    public static IdoResponse get(@Nonnull @JsonProperty("apiVersion") String apiVersion,
                                  @Nullable @JsonProperty("errorMessage") String errorMessage,
                                  @Nonnull @JsonProperty("payload") JsonNode payload) {
        return new AutoValue_IdoResponse(apiVersion, Optional.ofNullable(errorMessage), payload);
    }

    @Nonnull
    public abstract String getApiVersion();

    @Nonnull
    public abstract Optional<String> getErrorMessage();

    @Nonnull
    public abstract JsonNode getPayload();

}
