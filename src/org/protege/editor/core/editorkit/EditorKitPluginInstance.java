package org.protege.editor.core.editorkit;

import org.protege.editor.core.plugin.ProtegePluginInstance;

public interface EditorKitPluginInstance extends ProtegePluginInstance {
	
	void setup(EditorKit editorKit);
}
