package org.protege.editor.core.ui.workspace.tabs;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;

public class DeleteCustomTabsAction extends ProtegeAction {
	private static final long serialVersionUID = 5921291642080458478L;
	
	private JMenu parentMenu;
	private MenuListener listener = new MenuListener() {
		
		public void menuSelected(MenuEvent e) {
			updateEnabledStatus();
		}
		
		public void menuDeselected(MenuEvent e) { }

		public void menuCanceled(MenuEvent e) { }
	};

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
		TabbedWorkspace workspace = (TabbedWorkspace) getWorkspace();
		setEnabled(!workspace.getCustomTabsManager().getCustomTabs().isEmpty());
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
