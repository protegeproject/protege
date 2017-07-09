package org.protege.editor.core.ui.workspace;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.protege.editor.core.ui.tabbedpane.CloseableTabbedPaneUI;
import org.protege.editor.core.ui.tabbedpane.WorkspaceTabCloseHandler;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Medical Informatics Group<br> Date: Mar 17,
 * 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br> www.cs.man.ac.uk/~horridgm<br><br>

 * A <code>TabbedWorkspace</code> represents a <code>Workspace</code> that contains tabs.  The tabs are loaded
 * automatically depending on the <code>Workspace</code> editor kit.
 */
public abstract class TabbedWorkspace extends Workspace {


    private final Logger logger = LoggerFactory.getLogger(TabbedWorkspace.class);

    private final JTabbedPane tabbedPane = new JTabbedPane();

    private final Set<WorkspaceTab> workspaceTabs = new HashSet<>();
    
    private TabViewable checkPermissionLevel;
    
    public void setCheckLevel(TabViewable tvw) {
    	checkPermissionLevel = tvw;
    }


    public TabbedWorkspace() {
        tabbedPane.setUI(new CloseableTabbedPaneUI(CloseableTabbedPaneUI.TabClosability.CLOSABLE, new WorkspaceTabCloseHandler()));
        JPanel tabHolder = new JPanel(new BorderLayout());
        tabHolder.add(tabbedPane);
        setContent(tabbedPane);
    }

    public void initialise() {

        // Here we either need to load the default tabs, or
        // restore a set of tabs
        final List<String> visibleTabs = new TabbedWorkspaceStateManager().getTabs();

        // If no tabs are set as visible (i.e. we have yet to customise) show all by default
        for (WorkspaceTabPlugin plugin : getOrderedPlugins()) {
            if(visibleTabs.isEmpty()) {
                if(plugin.isProtegeDefaultTab()) {
                    addTabForPlugin(plugin);
                }
            }
            else if (visibleTabs.contains(plugin.getId())) {
                addTabForPlugin(plugin);
            }
        }
    }
    
    public boolean isReadOnly(ViewComponentPlugin vc) {
    	if (this.checkPermissionLevel != null) {
    		return checkPermissionLevel.isReadOnly(vc);
    	}
    	return false;
    }

   public boolean canShow(WorkspaceTabPlugin plugin) {
    	
    	if (this.checkPermissionLevel != null) {
    		return checkPermissionLevel.checkViewable(plugin);
    	}
    	return true;

    }
   
   public boolean isRequired(WorkspaceTabPlugin plugin) {
   	
   	if (this.checkPermissionLevel != null) {
   		return checkPermissionLevel.isRequired(plugin);
   	}
   	return true;

   }
   
   public boolean canShow(ViewComponentPlugin plugin) {
   	
   	if (this.checkPermissionLevel != null) {
   		return checkPermissionLevel.checkViewable(plugin);
   	}
   	return true;

   }
    
    public void recheckPlugins() {
    	
        // If no tabs are set as visible (ie we have yet to customise, show all by default
        for (WorkspaceTabPlugin plugin : getOrderedPlugins()) {
        	WorkspaceTab tab = getWorkspaceTab(plugin.getId());
        	if (canShow(plugin) && isRequired(plugin)) {         		
        		if (tab == null) {
        			try {
						tab = plugin.newInstance();
						this.addTab(tab);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			
        		}
        		
        	} else {
        		if (tab != null) {
            		// tab is currently showing, but shouldn't
        			removeTab(tab);
                    try {
    					tab.dispose();
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            		
                }                
        	}
        	
        }
    	
    }

    public WorkspaceTab addTabForPlugin(WorkspaceTabPlugin plugin) {
        WorkspaceTab tab = null;
        try {
            tab = plugin.newInstance();
            addTab(tab);
        }
        catch (Throwable e) {
            if (tab != null) {
                String msg = "An error occurred when creating the " + plugin.getLabel() + " tab.";
                tab.setLayout(new BorderLayout());
                tab.add(ComponentFactory.createExceptionComponent(msg, e, null));
            }
            logger.error("An error occurred when attempting to instantiate a tab plugin.  " +
                    "Tab plugin Id: {}.  Error details: {}",
                    plugin.getId(),
                    e);
        }
        return tab;
    }


    public void save() {
        try {
            super.save();
            // Save out tabs
            TabbedWorkspaceStateManager man = new TabbedWorkspaceStateManager(this);
            man.save();
            for (WorkspaceTab tab : getWorkspaceTabs()){
                tab.save();
                logger.info("Saved tab state for '{}' tab", tab.getLabel());
            }
            logger.info("Saved workspace");
        }
        catch (Exception e) {
            logger.error("An error occurred whilst saving the workspace", e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public List<WorkspaceTabPlugin> getOrderedPlugins() {
        WorkspaceTabPluginLoader loader = new WorkspaceTabPluginLoader(this);
        List<WorkspaceTabPlugin> plugins = new ArrayList<>(loader.getPlugins());
        CustomWorkspaceTabsManager customTabsManager = getCustomTabsManager();
        plugins.addAll(customTabsManager.getCustomTabPlugins(this));
        Collections.sort(plugins, (o1, o2) -> o1.getIndex().compareTo(o2.getIndex()));
        return plugins;
    }


    public CustomWorkspaceTabsManager getCustomTabsManager() {
        return new CustomWorkspaceTabsManager();
    }

    /**
     * Convenience method to add a workspace tab.  This method initialises the tab.
     */
    public void addTab(final WorkspaceTab workspaceTab) {
        tabbedPane.addTab(workspaceTab.getLabel(), workspaceTab.getIcon(), workspaceTab);
        workspaceTabs.add(workspaceTab);
        try {
            workspaceTab.initialise();
        }
        catch (Exception e) {
            tabbedPane.remove(workspaceTab);
            tabbedPane.addTab(workspaceTab.getLabel(), workspaceTab.getIcon(), createErrorPanel(e));
        }
    }

    public boolean containsTab(String tabId) {
        for (WorkspaceTab tab : getWorkspaceTabs()) {
            if (tab.getId().equals(tabId)) {
                return true;
            }
        }
        return false;
    }


    public int getTabCount() {
        return tabbedPane.getTabCount();
    }


    public void setSelectedTab(int index) {
        tabbedPane.setSelectedIndex(index);
    }


    private JComponent createErrorPanel(Exception e) {
        return ComponentFactory.createExceptionComponent(e.getMessage(), e, null);
    }


    /**
     * Convenience method to remove a workspace tab. If the tab is discarded then its <code>dispose()</code> method must
     * be called.
     *
     * @param workspaceTab The tab to be removed.
     */
    public void removeTab(WorkspaceTab workspaceTab) {
        tabbedPane.remove(workspaceTab);
        workspaceTabs.remove(workspaceTab);
    }


    public void setSelectedTab(WorkspaceTab workspaceTab) {
        tabbedPane.setSelectedComponent(workspaceTab);
    }


    public WorkspaceTab getSelectedTab() {
        return (WorkspaceTab) tabbedPane.getSelectedComponent();
    }


    public Set<WorkspaceTab> getWorkspaceTabs() {
        return Collections.unmodifiableSet(workspaceTabs);
    }


    public WorkspaceTab getWorkspaceTab(String id) {
        for (WorkspaceTab tab : getWorkspaceTabs()) {
            if (tab.getId().equals(id)) {
                return tab;
            }
        }
        return null;
    }


    public abstract WorkspaceTab createWorkspaceTab(String name);


    /**
     * Disposes of the tabbed workspace.  This removes any tabs in the workspace and disposes of them.
     */
    @Override
    public void dispose() {
        save();
        // Remove the tabs and call their dispose method
        for (WorkspaceTab tab : workspaceTabs) {
            try {
                tab.dispose();
                logger.info("Disposed of '{}' tab", tab.getLabel());
            }
            catch (Exception e) {
                logger.warn("The {} tab threw an exception whilst being disposed.", tab.getLabel(), e);
            }
        }
        workspaceTabs.clear();
        tabbedPane.removeAll();
        super.dispose();
        logger.info("Disposed of workspace");
    }
}