package org.protege.editor.core.ui.workspace;

import org.coode.mdock.DynamicConfigPanel;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginLoader;

import java.util.*;


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

    private Set<WorkspaceViewsTab> registeredTabs = new HashSet<>();

    private Map<String, ViewComponentPlugin> pluginMap = new HashMap<>();


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


    public ViewComponentPlugin getViewComponentPlugin(String id){
        return pluginMap.get(id);
    }
    

    public List<ViewComponentPlugin> getViewComponentPlugins() {
        List<ViewComponentPlugin> list = new ArrayList<>();
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
