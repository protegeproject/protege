package org.protege.editor.core.ui.explanationpreferences;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractApplicationPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
import org.protege.editor.core.ui.preferences.PreferencesPanelPlugin;

public class ExplanationPreferencesPanelPluginLoader
		extends AbstractApplicationPluginLoader<PreferencesPanelPlugin> {

    private EditorKit editorKit;


    public ExplanationPreferencesPanelPluginLoader(EditorKit editorKit) {
        super(ExplanationPreferencesPanelPluginJPFImpl.ID);
        this.editorKit = editorKit;
    }


    protected PluginExtensionMatcher getExtensionMatcher() {
        return new PluginParameterExtensionMatcher();
    }


    protected PreferencesPanelPlugin createInstance(IExtension extension) {
        return new ExplanationPreferencesPanelPluginJPFImpl(extension, editorKit);
    }
    
}
