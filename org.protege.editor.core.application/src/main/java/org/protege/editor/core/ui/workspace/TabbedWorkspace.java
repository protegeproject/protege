package org.protege.editor.core.ui.workspace;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.tabbedpane.WorkspaceTabCloseHandler;
import org.protege.editor.core.ui.tabbedpane.CloseableTabbedPaneUI;
import org.protege.editor.core.ui.util.ComponentFactory;

/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Medical Informatics Group<br> Date: Mar 17,
 * 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br> www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A <code>TabbedWorkspace</code> represents a <code>Workspace</code> that contains tabs.  The tabs are loaded
 * automatically depending on the <code>Workspace</code> clsdescriptioneditor kit.
 */
public abstract class TabbedWorkspace extends Workspace {

    private static final long serialVersionUID = 9179999766960877420L;

    private JTabbedPane tabbedPane;

    private Set<WorkspaceTab> workspaceTabs;

    private static final Logger LOGGER = Logger.getLogger(TabbedWorkspace.class);

    /**
     * Override of the <code>Workspace</code> initialise method.
     */
    public void initialise() {
        JPanel tabHolder = new JPanel(new BorderLayout());

        workspaceTabs = new HashSet<>();

        // Create the tabs.
        tabbedPane = new JTabbedPane();
        tabbedPane.setUI(new CloseableTabbedPaneUI(CloseableTabbedPaneUI.TabClosability.CLOSABLE, new WorkspaceTabCloseHandler()));

        tabHolder.add(tabbedPane);
        setContent(tabbedPane);

        // Here we either need to load the default tabs, or
        // restore a set of tabs
        final List<String> visibleTabs = new TabbedWorkspaceStateManager().getTabs();

        // If no tabs are set as visible (ie we have yet to customise, show all by default
        for (WorkspaceTabPlugin plugin : getOrderedPlugins()) {
            if (visibleTabs.isEmpty() || visibleTabs.contains(plugin.getId())) {
                addTabForPlugin(plugin);
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
            LOGGER.warn(e);
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
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception caught doing save", e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public List<WorkspaceTabPlugin> getOrderedPlugins() {
        WorkspaceTabPluginLoader loader = new WorkspaceTabPluginLoader(this);
        List<WorkspaceTabPlugin> plugins = new ArrayList<WorkspaceTabPlugin>(loader.getPlugins());
        CustomWorkspaceTabsManager customTabsManager = getCustomTabsManager();
        plugins.addAll(customTabsManager.getCustomTabPlugins(this));

        Collections.sort(plugins, new Comparator<WorkspaceTabPlugin>() {
            public int compare(WorkspaceTabPlugin o1, WorkspaceTabPlugin o2) {
                return o1.getIndex().compareTo(o2.getIndex());
            }
        });

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
            }
            catch (Exception e) {
                LOGGER.warn("BAD TAB: " + tab.getClass().getSimpleName() + " - Exception during dispose: " + e.getMessage());
            }
        }
        workspaceTabs.clear();
        tabbedPane.removeAll();
        super.dispose();
    }
}