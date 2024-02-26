package org.protege.editor.core.ui.action.start;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.protege.editor.core.plugin.ProtegePluginInstance;

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
