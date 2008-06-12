package org.protege.editor.core.ui.workspace;

import org.apache.log4j.Logger;
import org.protege.editor.core.FileManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.Resettable;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
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

    private static final String TAB_CUSTOM_NAME = "custom-";

    private JTabbedPane tabbedPane;

    private JMenu tabMenu;

    private Set<WorkspaceTab> workspaceTabs;

    private static final Logger logger = Logger.getLogger(TabbedWorkspace.class);

    private AbstractAction resetTabAction;

    private static final String VIEWCONFIG_PREFIX = "viewconfig-";

    private static final String VIEWCONFIG_EXTENSION = ".xml";


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

        for (WorkspaceTabPlugin plugin : getOrderedPlugins()) {
            if (visibleTabs.isEmpty() || visibleTabs.contains(plugin.getId())) {
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
            }
        }
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


    private List<WorkspaceTabPlugin> getOrderedPlugins() {
        WorkspaceTabPluginLoader loader = new WorkspaceTabPluginLoader(this);
        List<WorkspaceTabPlugin> plugins = new ArrayList<WorkspaceTabPlugin>(loader.getPlugins());

        plugins.addAll(generatePluginsFromCustomViewConfigs());

        Collections.sort(plugins, new Comparator<WorkspaceTabPlugin>() {
            public int compare(WorkspaceTabPlugin o1, WorkspaceTabPlugin o2) {
                return o1.getIndex().compareTo(o2.getIndex());
            }
        });

        return plugins;
    }


    // fake up a set of additional plugins from viewconfig files that have been added by the user.
    // The plugins get their identifiers from the name of the file
    private Set<WorkspaceTabPlugin> generatePluginsFromCustomViewConfigs() {
        Set<WorkspaceTabPlugin> plugins = new HashSet<WorkspaceTabPlugin>();

        File viewConfigFolder = FileManager.getViewConfigurationsFolder();
        if (viewConfigFolder.exists()){
            List<File> customTabFiles = Arrays.asList(viewConfigFolder.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return (file.getName().startsWith(VIEWCONFIG_PREFIX + TAB_CUSTOM_NAME) && file.getName().endsWith(VIEWCONFIG_EXTENSION));
                }
            }));

            for (File customTabFile : customTabFiles){
                final String label = customTabFile.getName().replaceFirst(VIEWCONFIG_PREFIX + TAB_CUSTOM_NAME, "").replace(VIEWCONFIG_EXTENSION, "");
                plugins.add(new CustomTabPlugin(label));
            }
        }
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


    private void handleCreateNewTab() {
        final String name = JOptionPane.showInputDialog(this, "Please enter a name for the new tab");
        if (name != null){
            addTab(new CustomTab(name));
            rebuildTabMenu();
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


    class CustomTabPlugin implements WorkspaceTabPlugin{

        private String label;


        CustomTabPlugin(String label) {
            this.label = label;
        }


        public TabbedWorkspace getWorkspace() {
            return TabbedWorkspace.this;
        }


        public String getLabel() {
            return label;
        }


        public Icon getIcon() {
            return null;
        }


        public String getIndex() {
            return "Z";
        }


        public URL getDefaultViewConfigFile() {
            return null;
        }


        public String getId() {
            return TAB_CUSTOM_NAME + getLabel();
        }


        public String getDocumentation() {
            return "";
        }


        public WorkspaceTab newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            return new CustomTab(label);
        }
    }

    class CustomTab extends WorkspaceViewsTab {

        private String name;


        public CustomTab(String name) {
            this.name = name;
        }


        public String getLabel() {
            return name;
        }

        public String getId() {
            return TAB_CUSTOM_NAME + getLabel();
        }

        public TabbedWorkspace getWorkspace() {
            return TabbedWorkspace.this;
        }

        public URL getDefaultViewConfigurationFile() {
            return null;
        }
    }
}
