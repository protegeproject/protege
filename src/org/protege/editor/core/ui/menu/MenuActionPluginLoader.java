package org.protege.editor.core.ui.menu;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.OrPluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
import org.protege.editor.core.plugin.PluginProperties;

public class MenuActionPluginLoader extends AbstractPluginLoader<MenuActionPlugin> {
    private EditorKit editorKit;
    
    public MenuActionPluginLoader(EditorKit editorKit) {
        super(ProtegeApplication.ID, MenuActionPluginJPFImpl.EXTENSION_POINT_ID);
        this.editorKit = editorKit;
    }

    @Override
    protected MenuActionPlugin createInstance(IExtension extension) {
        return new MenuActionPluginJPFImpl(editorKit, extension);
    }

    @Override
    protected PluginExtensionMatcher getExtensionMatcher() {
        // Load general items that are available for any clsdescriptioneditor kit
        PluginParameterExtensionMatcher generalMatcher = new PluginParameterExtensionMatcher();
        generalMatcher.put(PluginProperties.EDITOR_KIT_PARAM_NAME, "any");
        // Load items that are specific to the current clsdescriptioneditor kit
        PluginParameterExtensionMatcher specificMatcher = new PluginParameterExtensionMatcher();
        specificMatcher.put(PluginProperties.EDITOR_KIT_PARAM_NAME, editorKit.getId());
        return new OrPluginExtensionMatcher(generalMatcher, specificMatcher);
    }

}
