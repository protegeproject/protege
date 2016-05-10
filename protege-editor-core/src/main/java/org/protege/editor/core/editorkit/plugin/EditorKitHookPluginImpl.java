package org.protege.editor.core.editorkit.plugin;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractProtegePlugin;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public class EditorKitHookPluginImpl extends AbstractProtegePlugin<EditorKitHook> implements EditorKitHookPlugin {


    private EditorKit editorKit;

    public EditorKitHookPluginImpl(IExtension iExtension, EditorKit editorKit) {
    	super(iExtension);
        this.editorKit = editorKit;
    }


    public EditorKitHook newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                         InstantiationException {
        EditorKitHook instance = super.newInstance();
        instance.setup(editorKit);
        return instance;
    }
}
