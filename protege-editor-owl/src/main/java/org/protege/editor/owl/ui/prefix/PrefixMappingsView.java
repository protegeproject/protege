package org.protege.editor.owl.ui.prefix;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-10
 */
public interface PrefixMappingsView {

    interface PrefixMappingsChangedHandler {
        void handlePrefixMappingsChanged();
    }

    void setPrefixMappingsChangedHandler(@Nonnull PrefixMappingsChangedHandler handler);

    void setPrefixMappings(@Nonnull List<PrefixMapping> prefixMappings);

    @Nonnull
    ImmutableList<PrefixMapping> getPrefixMappings();
}
