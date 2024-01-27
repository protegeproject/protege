package org.protege.editor.owl.model.lang;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-03-15
 */
@AutoValue
public abstract class LangCode {

    @JsonCreator
    public static LangCode get(@JsonProperty("langCode") @Nonnull String langCode,
                               @JsonProperty("description") @Nonnull String description) {
        return new AutoValue_LangCode(langCode, description);
    }

    /**
     * Gets the languages code.
     */
    @Nonnull
    public abstract String getLangCode();

    /**
     * Gets a description of the language code
     */
    @Nonnull
    public abstract String getDescription();
}
