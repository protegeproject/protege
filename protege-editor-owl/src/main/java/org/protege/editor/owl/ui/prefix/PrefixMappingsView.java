package org.protege.editor.owl.ui.prefix;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.List;

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
