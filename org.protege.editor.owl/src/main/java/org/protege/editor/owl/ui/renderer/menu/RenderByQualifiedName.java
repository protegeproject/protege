package org.protege.editor.owl.ui.renderer.menu;

import org.protege.editor.owl.ui.renderer.OWLEntityQNameRenderer;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;

public class RenderByQualifiedName extends AbstractByRendererMenu {
	private static final long serialVersionUID = 7364664042805363135L;

	protected boolean isMyRendererPlugin(RendererPlugin plugin) {
		return plugin.getRendererClassName().equals(OWLEntityQNameRenderer.class.getName());
	}
	
	@Override
	protected boolean isConfigured(RendererPlugin plugin) {
		return true;
	}
	
	@Override
	protected void configure(RendererPlugin plugin) {
		;
	}
}
