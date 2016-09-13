package org.protege.editor.core.editorkit;

import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.editorkit.plugin.EditorKitHookPlugin;
import org.protege.editor.core.editorkit.plugin.EditorKitHookPluginLoader;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 13 Sep 16
 */
public class Initializers {

    /**
     * Instantiate, initialise and install EditorKitHook plugins for the specified EditorKit
     * @param editorKit The EditorKit.  Not {@code null}.
     */
    public static void loadEditorKitHooks(EditorKit editorKit) {
        if(editorKit == null) {
            throw new RuntimeException("EditorKit must not be null");
        }
        for (EditorKitHookPlugin editorKitHookPlugin : new EditorKitHookPluginLoader(editorKit).getPlugins()) {
            try {
                EditorKitHook instance = editorKitHookPlugin.newInstance();
                instance.initialise();
                editorKit.put(editorKitHookPlugin.getId(), instance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
