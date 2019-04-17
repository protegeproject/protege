package org.protege.editor.owl.model.identifiers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-16
 */
@AutoValue
public abstract class IdoCollection {

    @JsonCreator
    public static IdoCollection get(@Nonnull @JsonProperty("id") String id,
                                    @Nonnull @JsonProperty("prefix") String prefix,
                                    @Nonnull @JsonProperty("name") String name,
                                    @Nonnull @JsonProperty("pattern") String pattern,
                                    @Nonnull @JsonProperty("definition") String definition,
                                    @Nullable @JsonProperty("synonyms") ImmutableList<String> synonyms
                                    ) {
        return new AutoValue_IdoCollection(id, prefix, name, pattern, definition, synonyms == null ? ImmutableList.of() : synonyms);
    }

    @Nonnull
    public abstract String getId();

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
    public abstract String getDefinition();

    @Nonnull
    public abstract ImmutableList<String> getSynonyms();

}
