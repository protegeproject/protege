package org.protege.editor.core.ui.menu;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.EditorKitExtensionMatcher;
import org.protege.editor.core.plugin.PluginExtensionMatcher;

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
    	return new EditorKitExtensionMatcher(editorKit);
    }

}
