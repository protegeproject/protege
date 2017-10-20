package org.protege.editor.owl.ui.renderer.menu;

import org.protege.editor.owl.ui.prefix.OWLEntityPrefixedNameRenderer;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;

public class RenderByPrefixedName extends AbstractByRendererMenu {

	protected boolean isMyRendererPlugin(RendererPlugin plugin) {
		return plugin.getRendererClassName().equals(OWLEntityPrefixedNameRenderer.class.getName());
	}
	
	@Override
	protected boolean isConfigured(RendererPlugin plugin) {
		return true;
	}
	
	@Override
	protected void configure(RendererPlugin plugin) {
	}
}
