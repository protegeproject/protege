package org.protege.editor.owl.ui.renderer.plugin;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.owl.ProtegeOWL;

public class RendererPluginLoader extends AbstractPluginLoader<RendererPlugin> {
	
	public RendererPluginLoader() {
		super(ProtegeOWL.ID, RendererPlugin.RENDERER_PLUGIN_ID);
	}
	
	@Override
	protected RendererPlugin createInstance(IExtension extension) {
		return new RendererPlugin(extension);
	}
	
}
