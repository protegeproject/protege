package org.protege.editor.core.ui.action.start;

import org.protege.editor.core.plugin.ProtegePluginInstance;

import javax.swing.*;

public abstract class AltStartupAction extends AbstractAction implements ProtegePluginInstance {
	private static final long serialVersionUID = -8693527745746711830L;
	private JFrame parent;
	
	
	public JFrame getParent() {
		return parent;
	}
	public void setParent(JFrame parent) {
		this.parent = parent;
	}
	
	
	
}
