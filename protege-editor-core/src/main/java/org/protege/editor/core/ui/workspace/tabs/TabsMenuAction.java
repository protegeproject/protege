package org.protege.editor.core.ui.workspace.tabs;

import org.protege.editor.core.ui.action.ProtegeDynamicAction;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TabsMenuAction extends ProtegeDynamicAction {

    private static final long serialVersionUID = -3107456825784658749L;

	private final Logger logger = LoggerFactory.getLogger(TabsMenuAction.class);

	public void initialise() throws Exception {		
	}

	public void dispose() throws Exception {		
	}

	public void actionPerformed(ActionEvent e) {		
	}

	public void rebuildChildMenuItems(JMenu thisMenuItem) {
		if (!(getWorkspace() instanceof TabbedWorkspace)) {
			// Don't bother to show a tabs menu for non
			// tabbed workspaces.
			return;
		}
		thisMenuItem.removeAll();
		for (final WorkspaceTabPlugin plugin : ((TabbedWorkspace) getWorkspace()).getOrderedPlugins()) {
			addMenuItem(thisMenuItem, plugin);
		}

	}

	private boolean canShowTab(WorkspaceTabPlugin plugin) {

		return ((TabbedWorkspace) getWorkspace()).canShow(plugin);

	}

    private void addMenuItem(JMenu thisMenuItem, final WorkspaceTabPlugin plugin) {
        final TabbedWorkspace workspace = (TabbedWorkspace) getWorkspace();
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(new AbstractAction(plugin.getLabel()) {
            private static final long serialVersionUID = 2331248705801798457L;


            public void actionPerformed(ActionEvent e) {
                try {
                    if (!workspace.containsTab(plugin.getId())) {
                        WorkspaceTab tab = plugin.newInstance();
                        workspace.addTab(tab);
                    }
                    else {
                        WorkspaceTab tab = workspace.getWorkspaceTab(plugin.getId());
                        workspace.removeTab(tab);
                        tab.dispose();
                    }
                }
                catch (Exception ex) {
                    logger.error("An error occurred whilst adding a menu item.  Details: {}", ex);
                }
            }
        });
        item.setSelected(workspace.containsTab(plugin.getId()));
        if (canShowTab(plugin)) {
        	
        } else {
        	item.setEnabled(false);
        }
        
      
        thisMenuItem.add(item);
    }
	
}
