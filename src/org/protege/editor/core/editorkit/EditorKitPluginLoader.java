package org.protege.editor.core.editorkit;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.OrPluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
import org.protege.editor.core.plugin.PluginProperties;

public class EditorKitPluginLoader extends AbstractPluginLoader<EditorKitPlugin> {
	private EditorKit editorKit;
	
	public EditorKitPluginLoader(EditorKit editorKit) {
		super(ProtegeApplication.ID, EditorKitPlugin.ID);
		this.editorKit = editorKit;
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

	protected EditorKitPlugin createInstance(IExtension extension) {
		return new EditorKitPlugin(editorKit, extension);
	}

}
