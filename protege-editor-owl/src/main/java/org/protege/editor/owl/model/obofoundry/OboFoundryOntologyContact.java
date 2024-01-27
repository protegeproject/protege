package org.protege.editor.owl.model.obofoundry;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
@AutoValue
public abstract class OboFoundryOntologyContact {

    private static final OboFoundryOntologyContact EMPTY = get("", "", "");

    @Nonnull
    @JsonCreator
    public static OboFoundryOntologyContact get(@JsonProperty("label") String label,
                                                @JsonProperty("email") String email,
                                                @JsonProperty("github") String githubId) {
        return new AutoValue_OboFoundryOntologyContact(orEmpty(label), orEmpty(email), orEmpty(githubId));
    }

    private static String orEmpty(String s) {
        return Objects.toString(s, "");
    }

    public static OboFoundryOntologyContact empty() {
        return EMPTY;
    }

    public boolean isEmpty() {
        return getEmail().isEmpty();
    }

    @JsonProperty("label")
    public abstract String getLabel();

    @JsonProperty("email")
    public abstract String getEmail();

    @JsonProperty("github")
    public abstract String getGithubId();
}
