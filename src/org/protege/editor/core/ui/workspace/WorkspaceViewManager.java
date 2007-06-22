package org.protege.editor.core.ui.workspace;

import org.coode.mdock.DynamicConfigPanel;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginLoader;

import java.util.*;
/*
 * Copyright (C) 2007, University of Manchester
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 29, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The view manager provides a place where plugins
 * can ask for certain views to be shown.
 */
public class WorkspaceViewManager {

    private Set<WorkspaceViewsTab> registeredTabs;

    private Map<String, ViewComponentPlugin> pluginMap;


    public WorkspaceViewManager() {
        pluginMap = new HashMap<String, ViewComponentPlugin>();
        registeredTabs = new HashSet<WorkspaceViewsTab>();
    }


    public void registerViews(WorkspaceViewsTab tab) {
        ViewComponentPluginLoader loader = new ViewComponentPluginLoader(tab.getWorkspace());
        Set<ViewComponentPlugin> plugins = loader.getPlugins();
        for (final ViewComponentPlugin plugin : plugins) {
            pluginMap.put(plugin.getId(), plugin);
        }
        registeredTabs.add(tab);
    }


    public void unregisterViews(WorkspaceViewsTab tab) {
//        for (Iterator<String> it = pluginMap.keySet().iterator(); it.hasNext();) {
//            String id = it.next();
//            ViewComponentPlugin plugin = pluginMap.get(id);
//            if (plugin.getWorkspaceTab().equals(tab)) {
//                it.remove();
//            }
//        }
//        registeredTabs.remove(tab);
    }


    public List<ViewComponentPlugin> getViewComponentPlugins() {
        List<ViewComponentPlugin> list = new ArrayList<ViewComponentPlugin>();
        list.addAll(pluginMap.values());
        return list;
    }


    /**
     * If the selected workspace tab is a views tab, then
     * this method requests that the view specified by the
     * view id is brought to the front.
     * @param viewId The id of the view to be shown.
     */
    public void bringViewToFront(String viewId) {
        ViewComponentPlugin plugin = pluginMap.get(viewId);
        if (plugin == null) {
            return;
        }
        // We need to get hold of the views tab
        Workspace ws = plugin.getWorkspace();
        if (ws instanceof TabbedWorkspace) {
            WorkspaceTab tab = ((TabbedWorkspace) ws).getSelectedTab();
            if (tab instanceof WorkspaceViewsTab) {
                ((WorkspaceViewsTab) tab).bringViewToFront(viewId);
            }
        }
    }


    /**
     * Shows the view that is identified by the specified id.
     * @param viewId The id of the view to be shown.
     * @return The <code>View</code> that was shown.  If the
     *         <code>View</code> could not be shown then the return
     *         value is <code>null</code>.
     */
    public View showView(String viewId) {
        ViewComponentPlugin plugin = pluginMap.get(viewId);
        if (plugin == null) {
            return null;
        }
        // We need to get hold of the views tab
        Workspace ws = plugin.getWorkspace();
        if (ws instanceof TabbedWorkspace) {
            WorkspaceTab tab = ((TabbedWorkspace) ws).getSelectedTab();
//            if (tab instanceof WorkspaceViewsTab) {
            View view = new View(plugin, plugin.getWorkspace());
            DynamicConfigPanel pan = new DynamicConfigPanel(tab);
            pan.setCurrentComponent(view, plugin.getLabel());
            pan.activate();
//                ((WorkspaceViewsTab) tab).getViewsPane().addView(view, plugin.getLabel());
//            }
        }
        return null;
    }
}
