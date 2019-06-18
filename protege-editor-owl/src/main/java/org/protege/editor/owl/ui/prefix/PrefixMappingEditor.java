package org.protege.editor.owl.ui.prefix;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-18
 */
public class PrefixMappingEditor {


    @Nonnull
    private final PrefixMappingEditorView view;

    public PrefixMappingEditor(@Nonnull PrefixMappingEditorView view) {
        this.view = checkNotNull(view);
        this.view.setPrefixNameChangedHandler(() -> {

        });
    }

    public PrefixMappingEditorView asJComponent() {
        return view.asJComponent();
    }

    public void clear() {
        view.clear();
    }

    public void setPrefixMapping(@Nonnull PrefixMapping prefixMapping) {
        view.setPrefixName(prefixMapping.getPrefixName());
        view.setPrefix(prefixMapping.getPrefix());
    }

    public Optional<PrefixMapping> getPrefixMapping() {
        String prefixName = view.getPrefixName().replace(" ", "");
        if(!prefixName.endsWith(":")) {
            prefixName = prefixName + ":";
        }
        String prefix = view.getPrefix().replace(" ", "");
        if(prefix.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(PrefixMapping.get(prefixName, prefix));
    }
}
