package org.protege.editor.core.editorkit;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;

public class EditorKitPlugin extends AbstractProtegePlugin<EditorKitPluginInstance> {
	public static final String ID = "editorKitPlugin";
	
	private EditorKit editorKit;

	protected EditorKitPlugin(EditorKit editorKit, IExtension extension) {
		super(extension);
		this.editorKit = editorKit;
	}
	
	public EditorKitPluginInstance newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		EditorKitPluginInstance plugin = super.newInstance();
		plugin.setup(editorKit);
		return plugin;
	}

}
