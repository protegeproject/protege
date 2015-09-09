package org.protege.editor.core.ui.workspace.tabs;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.workspace.CustomWorkspaceTabsManager;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTab;

public class CreateTabAction extends ProtegeAction {
	private static final long serialVersionUID = -4344479361094912711L;

	public void initialise() throws Exception {

	}

	public void dispose() throws Exception {

	}

	public void actionPerformed(ActionEvent e) {
		TabbedWorkspace workspace = (TabbedWorkspace) getWorkspace();
		handleCreateNewTab(workspace);
	}
	
	public static WorkspaceTab handleCreateNewTab(TabbedWorkspace workspace) {
        final String name = JOptionPane.showInputDialog(workspace, "Please enter a name for the new tab");
        if (name != null) {
        CustomWorkspaceTabsManager customTabsManager = workspace.getCustomTabsManager();
        WorkspaceTab tab = workspace.addTabForPlugin(customTabsManager.getPluginForTabName(name, workspace));
        workspace.setSelectedTab(tab);
        return tab;
        }
        return null;
	}
}
