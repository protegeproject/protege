package org.protege.editor.core.ui.workspace;

import org.apache.log4j.Logger;
import org.protege.editor.core.FileManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.Resettable;
import org.protege.editor.core.ui.util.UIUtil;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A <code>TabbedWorkspace</code> represents a
 * <code>Workspace</code> that contains tabs.  The tabs
 * are loaded automatically depending on the <code>Workspace</code>
 * clsdescriptioneditor kit.
 */
public abstract class TabbedWorkspace extends Workspace {

    public static final String TABS_MENU_NAME = "Tabs";

    private JTabbedPane tabbedPane;

    private JMenu tabMenu;

    private Set<WorkspaceTab> workspaceTabs;

    private static final Logger logger = Logger.getLogger(TabbedWorkspace.class);

    private AbstractAction resetTabAction;


    /**
     * Override of the <code>Workspace</code> initialise method.
     */
    public void initialise() {
        JPanel tabHolder = new JPanel(new BorderLayout());

        workspaceTabs = new HashSet<WorkspaceTab>();

        // Create the tabs.
        tabbedPane = new JTabbedPane();

        tabHolder.add(tabbedPane);
        setContent(tabbedPane);

        // Here we either need to load the default tabs, or
        // restore a set of tabs
        final List<String> visibleTabs = new TabbedWorkspaceStateManager().getTabs();


        Map<String, WorkspaceTabPlugin> map = new HashMap<String, WorkspaceTabPlugin>();
        for (WorkspaceTabPlugin plugin : getOrderedPlugins()) {
            if(visibleTabs.contains(plugin.getId())) {
                addTabForPlugin(plugin);
            }
        }
    }


    private WorkspaceTab addTabForPlugin(WorkspaceTabPlugin plugin) {
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
            Logger.getLogger(getClass().getName()).warn(e);
        }
        return tab;
    }


    public void save() {
        try {
            super.save();
            // Save out tabs
            TabbedWorkspaceStateManager man = new TabbedWorkspaceStateManager(this);
            man.save();
        }
        catch (Exception e) {
            Logger.getLogger(getClass()).error("Exception caught doing save", e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////
    ////  Tabs Menu


    protected void initialiseExtraMenuItems(JMenuBar menuBar) {
        super.initialiseExtraMenuItems(menuBar);

        createTabMenu(menuBar);

        tabMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                refreshActions();
                //rebuildTabMenu(); cannot see any reason to do this every time the menu is selected
            }

            public void menuDeselected(MenuEvent e) {
            }

            public void menuCanceled(MenuEvent e) {
            }
        });
        rebuildTabMenu();
    }


    // Add in menus for tabs
    private void createTabMenu(JMenuBar menuBar) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu.getText() != null) {
                if (menu.getText().equals(TABS_MENU_NAME)) {
                    tabMenu = menu;
                    break;
                }
            }
        }
        if (tabMenu == null) {
            tabMenu = new JMenu(TABS_MENU_NAME);
            menuBar.add(tabMenu);
        }
    }


    private void rebuildTabMenu() {
        tabMenu.removeAll();

        for (final WorkspaceTabPlugin plugin : getOrderedPlugins()) {
            addMenuItem(plugin);
        }

        tabMenu.addSeparator();
        tabMenu.add(new AbstractAction("Create new tab..."){
            public void actionPerformed(ActionEvent event) {
                handleCreateNewTab();
            }
        });

        tabMenu.addSeparator();
        tabMenu.add(new AbstractAction("Save current layout") {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        tabMenu.add(new AbstractAction("Export current tab...") {

            public void actionPerformed(ActionEvent e) {
                handleExportLayout();
            }
        });
        tabMenu.add(new AbstractAction("Import tab...") {

            public void actionPerformed(ActionEvent e) {
                handleImportLayout();
            }
        });

        tabMenu.addSeparator();
        tabMenu.add(resetTabAction = new AbstractAction("Reset selected tab to default state") {
            public void actionPerformed(ActionEvent e) {
                handleReset();
            }
        });
    }


    private void refreshActions() {
        resetTabAction.setEnabled(tabbedPane.getSelectedComponent() instanceof Resettable);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private List<WorkspaceTabPlugin> getOrderedPlugins() {
        WorkspaceTabPluginLoader loader = new WorkspaceTabPluginLoader(this);
        List<WorkspaceTabPlugin> plugins = new ArrayList<WorkspaceTabPlugin>(loader.getPlugins());
        CustomWorkspaceTabsManager customTabsManager = new CustomWorkspaceTabsManager();
        plugins.addAll(customTabsManager.getCustomTabPlugins(this));

        Collections.sort(plugins, new Comparator<WorkspaceTabPlugin>() {
            public int compare(WorkspaceTabPlugin o1, WorkspaceTabPlugin o2) {
                return o1.getIndex().compareTo(o2.getIndex());
            }
        });

        return plugins;
    }
    
    private void addMenuItem(final WorkspaceTabPlugin plugin) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(new AbstractAction(plugin.getLabel()) {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (!containsTab(plugin.getId())) {
                        WorkspaceTab tab = plugin.newInstance();
                        addTab(tab);
                    }
                    else {
                        WorkspaceTab tab = getWorkspaceTab(plugin.getId());
                        removeTab(tab);
                        tab.dispose();
                    }
                }
                catch (Exception ex) {
                    logger.error("Exception caught (re)building tab menu", ex);
                }
            }
        });
        item.setSelected(containsTab(plugin.getId()));
        tabMenu.add(item);
    }


    private void handleReset() {
        Resettable tab = (Resettable) tabbedPane.getSelectedComponent();
        tab.reset();
        tabbedPane.getSelectedComponent().validate();
    }


    private WorkspaceTab handleCreateNewTab() {
        final String name = JOptionPane.showInputDialog(this, "Please enter a name for the new tab");
        if (name != null){
            CustomWorkspaceTabsManager customTabsManager = new CustomWorkspaceTabsManager();
            WorkspaceTab tab = addTabForPlugin(customTabsManager.getPluginForTabName(name, this));
            rebuildTabMenu();
            setSelectedTab(tab);
            return tab;
        }
        return null;
    }

    private void handleExportLayout() {
        try {
            Set<String> extensions = new HashSet<String>();
            extensions.add("xml");
            String fileName = getSelectedTab().getLabel().replace(' ', '_') + ".layout.xml";
            File f = UIUtil.saveFile((Window) SwingUtilities.getAncestorOfClass(Window.class, this), "Save layout to", extensions,
                                     fileName);
            if(f == null) {
                return;
            }
            f.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(f);
            ((WorkspaceViewsTab) getSelectedTab()).getViewsPane().saveViews(writer);
            writer.close();
            JOptionPane.showMessageDialog(this, "Layout saved to: " + f);
        }
        catch (IOException e) {
            logger.error(e);
            JOptionPane.showMessageDialog(this, "There was a problem saving the layout", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleImportLayout() {
        try {
            Set<String> extensions = new HashSet<String>();
            extensions.add("xml");
            File f = UIUtil.openFile((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, this), "Save layout to", extensions);
            if(f == null) {
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            WorkspaceViewsTab tab = (WorkspaceViewsTab) handleCreateNewTab();
            if(tab == null) {
                return;
            }
            tab.reset(sb.toString());
        }
        catch (IOException e) {
            logger.error(e);
            JOptionPane.showMessageDialog(this, "There was a problem saving the layout", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Convenience method to add a workspace tab.  This
     * method initialises the tab.
     */
    public void addTab(WorkspaceTab workspaceTab) {
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
     * Convenience method to remove a workspace tab.
     * If the tab is discarded then its <code>dispose()</code>
     * method must be called.
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
     * Disposes of the tabbed workspace.  This removes any tabs
     * in the workspace and disposes of them.
     */
    public void dispose() {
        save();
        // Remove the tabs and call their dispose method
        for (WorkspaceTab tab : workspaceTabs){
            try {
                tab.dispose();
            }
            catch (Exception e) {
                logger.warn("BAD TAB: " + tab.getClass().getSimpleName() + " - Exception during dispose: " + e.getMessage());
            }
        }
        workspaceTabs.clear();
        tabbedPane.removeAll();
    }
}
