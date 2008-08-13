package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.net.URL;
import java.awt.*;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
    public WorkspaceTabPlugin getPluginForTabName(final String name, final TabbedWorkspace workspace) {
        if(!customTabs.contains(name)) {
            customTabs.add(name);
            save();
        }
        CustomTabPlugin plugin = new CustomTabPlugin(name, workspace);
        return plugin;
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
    private class CustomTabPlugin implements WorkspaceTabPlugin {

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
            CustomTab tab = new CustomTab(name);
            tab.setup(this);
            return tab;
        }

        private class CustomTab extends WorkspaceViewsTab{

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


            public void dispose() {
                super.dispose();
            }


            public void initialise() {
                super.initialise();
            }
        }
    }

}
