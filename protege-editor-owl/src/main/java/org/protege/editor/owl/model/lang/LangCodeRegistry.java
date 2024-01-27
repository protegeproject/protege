package org.protege.editor.owl.model.lang;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-03-15
 */
public class LangCodeRegistry {

    private ImmutableMap<String, LangCode> registry;

    private ImmutableList<LangCode> langCodes;

    private LangCodeRegistry(@Nonnull ImmutableList<LangCode> langCodes,
                             @Nonnull ImmutableMap<String, LangCode> registry) {
        this.langCodes = checkNotNull(langCodes);
        this.registry = checkNotNull(registry);
    }

    public Optional<LangCode> getLangCode(@Nonnull String langCode) {
        return Optional.ofNullable(registry.get(langCode));
    }

    public static LangCodeRegistry get() {
        ImmutableList<LangCode> langCodes = LangCodesResource.getLangCodes();
        ImmutableMap.Builder<String, LangCode> registryBuilder = ImmutableMap.builder();
        langCodes.forEach(lc -> registryBuilder.put(lc.getLangCode().toLowerCase(), lc));
        return new LangCodeRegistry(langCodes, registryBuilder.build());
    }

    public ImmutableList<LangCode> getLangCodes() {
        return langCodes;
    }


}
