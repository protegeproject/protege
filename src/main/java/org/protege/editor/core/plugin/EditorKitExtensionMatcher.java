package org.protege.editor.core.plugin;

import org.protege.editor.core.editorkit.EditorKit;

public class EditorKitExtensionMatcher extends OrPluginExtensionMatcher implements PluginExtensionMatcher {

	
	public EditorKitExtensionMatcher(EditorKit editorKit) {
		super(getDisjuncts(editorKit));
	}
	
	private static PluginExtensionMatcher[] getDisjuncts(EditorKit editorKit) {
        // Load general items that are available for any clsdescriptioneditor kit
        PluginParameterExtensionMatcher generalMatcher = new PluginParameterExtensionMatcher();
        generalMatcher.put(PluginProperties.EDITOR_KIT_PARAM_NAME, "any");
        // Load items that are specific to the current clsdescriptioneditor kit
        PluginParameterExtensionMatcher specificMatcher = new PluginParameterExtensionMatcher();
        specificMatcher.put(PluginProperties.EDITOR_KIT_PARAM_NAME, editorKit.getId());
        return new PluginExtensionMatcher[] { generalMatcher, specificMatcher };
	}
}
