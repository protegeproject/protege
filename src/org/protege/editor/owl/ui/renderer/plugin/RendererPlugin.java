package org.protege.editor.owl.ui.renderer.plugin;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractProtegePlugin;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.owl.ui.renderer.OWLModelManagerEntityRenderer;

public class RendererPlugin extends AbstractProtegePlugin<OWLModelManagerEntityRenderer> implements Comparable<RendererPlugin> {
	public static final String RENDERER_PLUGIN_ID = "entity_renderer";

	public RendererPlugin(IExtension extension) {
		super(extension);
	}
	
	public String getName() {
		return getPluginProperty("name");
	}
	
	public String getSortPosition() {
		return getPluginProperty("sortPosition");
	}
	
	public String getRendererClassName() {
		return getPluginProperty(PluginProperties.CLASS_PARAM_NAME);
	}
	
	public OWLModelManagerEntityRenderer newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		OWLModelManagerEntityRenderer renderer = super.newInstance();
		return renderer;
	}
	
	public int compareTo(RendererPlugin o) {
		return getSortPosition().compareTo(o.getSortPosition());
	}
}
