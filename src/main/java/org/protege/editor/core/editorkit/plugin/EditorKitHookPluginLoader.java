package org.protege.editor.core.editorkit.plugin;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractApplicationPluginLoader;
import org.protege.editor.core.plugin.EditorKitExtensionMatcher;
import org.protege.editor.core.plugin.PluginExtensionMatcher;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public class EditorKitHookPluginLoader extends AbstractApplicationPluginLoader<EditorKitHookPlugin> {

    private EditorKit editorKit;

    public EditorKitHookPluginLoader(EditorKit editorKit) {
        super(EditorKitHookPlugin.EXTENSION_POINT_ID);
        this.editorKit = editorKit;
    }

    @Override
    protected PluginExtensionMatcher getExtensionMatcher() {
    	return new EditorKitExtensionMatcher(editorKit);
    }

    protected EditorKitHookPlugin createInstance(IExtension extension) {
        return new EditorKitHookPluginImpl(extension, editorKit);
    }
}
