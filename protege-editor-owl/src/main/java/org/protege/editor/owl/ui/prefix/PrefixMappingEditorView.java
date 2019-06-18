package org.protege.editor.owl.ui.prefix;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-18
 */
public interface PrefixMappingEditorView {

    PrefixMappingEditorView asJComponent();

    interface PrefixNameChangedHandler {
        void handlePrefixNameChanged();
    }

    interface PrefixChangedHandler {
        void handlePrefixChanged();
    }

    void clear();

    void setPrefixName(@Nonnull String prefixName);

    @Nonnull
    String getPrefixName();

    void setPrefix(@Nonnull String prefix);

    String getPrefix();

    void setPrefixNameChangedHandler(@Nonnull PrefixNameChangedHandler handler);

    void setPrefixChangedHandler(@Nonnull PrefixChangedHandler handler);
}
