package org.protege.editor.core.ui.action.start;

import java.util.Set;
import java.util.TreeSet;

import javax.swing.JFrame;

import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.DefaultPluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginExtensionMatcher;

public class AltStartupActionPluginLoader extends AbstractPluginLoader<AltStartupActionPlugin> {

	private JFrame parent;
	
	public AltStartupActionPluginLoader(JFrame parent) {
		super(ProtegeApplication.ID, AltStartupActionPlugin.ID);
		this.parent = parent;
	}
	
	protected AltStartupActionPlugin createInstance(IExtension extension) {
		return new AltStartupActionPlugin(parent, extension);
	}
	
	@Override
	protected PluginExtensionMatcher getExtensionMatcher() {
		return new DefaultPluginExtensionMatcher();
	}
	
	@Override
	public Set<AltStartupActionPlugin> getPlugins() {
		return new TreeSet<>(super.getPlugins());
	}

}
