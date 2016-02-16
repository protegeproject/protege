package org.protege.editor.owl.ui.renderer.menu;

import org.protege.editor.owl.ui.renderer.OWLEntityRendererImpl;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;

public class RenderById extends AbstractByRendererMenu {
	private static final long serialVersionUID = 7364664042805363135L;

	protected boolean isMyRendererPlugin(RendererPlugin plugin) {
		return plugin.getRendererClassName().equals(OWLEntityRendererImpl.class.getName());
	}
	
	@Override
	protected boolean isConfigured(RendererPlugin plugin) {
		return true;
	}
	
	@Override
	protected void configure(RendererPlugin plugin) {
	}
}
