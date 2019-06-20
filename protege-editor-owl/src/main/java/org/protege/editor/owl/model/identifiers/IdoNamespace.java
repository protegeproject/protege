package org.protege.editor.owl.model.identifiers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-16
 */
@AutoValue
public abstract class IdoNamespace {

    @JsonCreator
    public static IdoNamespace get(@Nonnull @JsonProperty("prefix") String prefix,
                                   @Nonnull @JsonProperty("name") String name,
                                   @Nonnull @JsonProperty("pattern") String pattern,
                                   @Nonnull @JsonProperty("description") String description) {
        return new AutoValue_IdoNamespace(prefix, name, pattern, description);
    }

    @Nonnull
    public abstract String getPrefix();

    @Nonnull
    public abstract String getName();

    @Nonnull
    public abstract String getPattern();

    @Memoized
    @Nonnull
    public Pattern getCompiledPattern() {
        return Pattern.compile(getPattern());
    }

    @Nonnull
    public abstract String getDescription();


}
