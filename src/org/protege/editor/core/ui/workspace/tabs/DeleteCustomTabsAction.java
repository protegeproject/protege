package org.protege.editor.core.ui.workspace.tabs;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;

public class DeleteCustomTabsAction extends ProtegeAction {
	private static final long serialVersionUID = 5921291642080458478L;

	public void initialise() throws Exception {
	}

	public void dispose() throws Exception {
	}
	

	public void actionPerformed(ActionEvent event) {
		TabbedWorkspace workspace = (TabbedWorkspace) getWorkspace();
        LoadedTabsSelector selector = new LoadedTabsSelector(workspace);
        int ret = JOptionPaneEx.showConfirmDialog(workspace,
                                                  "Delete Tabs",
                                                  selector,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  selector);
        if (ret == JOptionPane.OK_OPTION){
            for (WorkspaceTabPlugin tabPlugin : selector.getSelectedTabs()){
                final String id = tabPlugin.getId();

                if (workspace.containsTab(id)) {
                    // make sure we remove it from the workspace if it is currently active
                    WorkspaceTab tab = workspace.getWorkspaceTab(id);
                    workspace.removeTab(tab);
                    try {
                        tab.dispose();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                
                workspace.getCustomTabsManager().deleteCustomTab(id);
            }
        }
	}

}
