package org.protege.editor.core.ui.workspace.tabs;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.Resettable;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTab;

public class ResetCurrentTabAction extends ProtegeAction {
	private static final long serialVersionUID = 6192614623601181816L;
	private JMenu parentMenu;
	private MenuListener listener = new MenuListener() {
		
		public void menuSelected(MenuEvent e) {
			updateEnabledStatus();
		}
		
		public void menuDeselected(MenuEvent e) { }

		public void menuCanceled(MenuEvent e) { }
	};

	public ResetCurrentTabAction() {
	}


	public void initialise() throws Exception {
	}
	
	public void dispose() throws Exception {
		if (parentMenu != null) {
			parentMenu.removeMenuListener(listener);
			parentMenu = null;
		}
	}
	
	public void setMenuParent(JComponent parent) {
		if (parent instanceof JMenu) {
			parentMenu = (JMenu) parent;
			parentMenu.addMenuListener(listener);
		}
	}

	private void updateEnabledStatus() {
		setEnabled(((TabbedWorkspace) getWorkspace()).getSelectedTab() instanceof Resettable);
	}
	
	public void actionPerformed(ActionEvent e) {
        WorkspaceTab tab = ((TabbedWorkspace) getWorkspace()).getSelectedTab();
        ((Resettable) tab).reset();
        tab.validate();
	}

}
