package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 13-Aug-2008<br><br>
 *
 * Manages custom tabs.  A custom tab is a tab that has not been pre-defined in
 * a plugin.xml file.  Note that the ID of a custom tab is the same as the name
 * of the tab.
 */
public class CustomWorkspaceTabsManager {

    private List<String> customTabs;

    private static final String CUSTOM_TABS_PREFERENCES_KEY = "custom_tabs";

    private Preferences prefs;

    public CustomWorkspaceTabsManager() {
        prefs = PreferencesManager.getInstance().getApplicationPreferences(CUSTOM_TABS_PREFERENCES_KEY);
        customTabs = prefs.getStringList(CUSTOM_TABS_PREFERENCES_KEY, new ArrayList<String>());
    }


    /**
     * Gets a list of the custom tabs that have been defined.
     * @return A list of names/ids of custom tabs
     */
    public List<String> getCustomTabs() {
        return Collections.unmodifiableList(customTabs);
    }


    /**
     * Gets the plugins that define the custom tabs.  (For what it's worth,
     * these plugins are created on the fly - they are not defined in any
     * plugin.xml file.
     * @param workspace The workspace that the tab plugins pertain to.
     * @return A list of custom tab plugins
     */
    public List<WorkspaceTabPlugin> getCustomTabPlugins(TabbedWorkspace workspace) {
        List<WorkspaceTabPlugin> tabPlugins = new ArrayList<WorkspaceTabPlugin>();
        for(String tab : customTabs) {
            tabPlugins.add(getPluginForTabName(tab, workspace));
        }
        return tabPlugins;
    }

    private void save() {
        prefs.putStringList(CUSTOM_TABS_PREFERENCES_KEY, customTabs);
    }


    /**
     * Gets a custom tab plugin for a custom tab.
     * @param name The name/id of the custom tab.
     * @param workspace The workspace pertaining to the tab
     * @return A WorkspaceTabPlugin that can be used to create a custom tab.
     */
    public final WorkspaceTabPlugin getPluginForTabName(final String name, final TabbedWorkspace workspace) {
        if(!customTabs.contains(name)) {
            customTabs.add(name);
            save();
        }
        return createPlugin(name, workspace);
    }


    protected WorkspaceTabPlugin createPlugin(String name, TabbedWorkspace workspace) {
        return new CustomTabPlugin(name, workspace);
    }


    /**
     * Deletes a previously added custom tab
     * @param id The name/id of the custom tab to be deleted.
     */
    public void deleteCustomTab(String id) {
        customTabs.remove(id);
        save();
    }


    /**
     * Determines if the specified name/id corresponds to the
     * name/id of a custom tab.
     * @param id The id/name of the tab
     * @return <code>true</code> if the id/name is of a custom tab, otherwise
     * <code>false</code>
     */
    public boolean isCustomTab(String id) {
        return customTabs.contains(id);
    }


    /**
     * A class that acts as a dynamic plugin
     */
    protected class CustomTabPlugin implements WorkspaceTabPlugin {

        private TabbedWorkspace workspace;

        private String name;


        public CustomTabPlugin(String name, TabbedWorkspace workspace) {
            this.name = name;
            this.workspace = workspace;
        }


        public URL getDefaultViewConfigFile() {
            // By definition, there is no default view config file.
            return null;
        }


        public Icon getIcon() {
            return null;
        }


        public String getIndex() {
            return "Z";
        }


        public String getLabel() {
            return name;
        }


        public TabbedWorkspace getWorkspace() {
            return workspace;
        }


        public String getDocumentation() {
            return "Custom tab: " + getLabel();
        }


        public String getId() {
            return name;
        }


        public WorkspaceTab newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                 InstantiationException {
            WorkspaceViewsTab tab = createCustomTab(name);
            tab.setup(this);
            return tab;
        }


        protected WorkspaceViewsTab createCustomTab(String label) {
            return new CustomTab(label);
        }


        private class CustomTab extends WorkspaceViewsTab{

            /**
             * 
             */
            private static final long serialVersionUID = -4411850900268388198L;
            private String label;

            public CustomTab(String label) {
                this.label = label;
                setToolTipText("Drop views from the Views menu to create a custom view layout");
            }


            public String getLabel() {
                return label;
            }


            public String getId() {
                return label;
            }
        }
    }

}
