package org.protege.editor.owl.ui.prefix;

import javax.annotation.Nonnull;

import com.google.auto.value.AutoValue;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-10
 */
@AutoValue
public abstract class PrefixMapping {

    @Nonnull
    public static PrefixMapping get(@Nonnull String prefixName,
                                    @Nonnull String prefix) {
        if(!prefixName.endsWith(":")) {
            throw new IllegalArgumentException("Prefix names must end with a colon");
        }
        return new AutoValue_PrefixMapping(prefixName, prefix);
    }

    @Nonnull
    public abstract String getPrefixName();

    @Nonnull
    public abstract String getPrefix();

}
