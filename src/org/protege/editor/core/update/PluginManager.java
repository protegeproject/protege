package org.protege.editor.core.update;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.progress.BackgroundTask;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PluginManager {

    private static final Logger logger = Logger.getLogger(PluginManager.class);

    private static PluginManager instance;

    public static final String AUTO_UPDATE_KEY = "CheckForUpdates";

    public static final String PLUGIN_REGISTRY_KEY = "plugin.registry-4.1.url";

    public static final String DEFAULT_REGISTRY = "http://smi-protege.stanford.edu/protege4/plugins/4.1-plugins.repository";

    private DownloadsProvider pluginRegistry;

    private UpdatesProvider updatesProvider;


    private PluginManager() {

    }


    public static synchronized PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager();
        }
        return instance;
    }


    private Preferences getPrefs() {
        PreferencesManager man = PreferencesManager.getInstance();
        return man.getApplicationPreferences(PluginManager.class);
    }


    public void setAutoUpdateEnabled(boolean b) {
        getPrefs().putBoolean(AUTO_UPDATE_KEY, b);
    }


    public boolean isAutoUpdateEnabled() {
        return getPrefs().getBoolean(AUTO_UPDATE_KEY, true);
    }


    public URL getPluginRegistryLocation() {
        String pluginRegistryLoc = getPrefs().getString(PLUGIN_REGISTRY_KEY, DEFAULT_REGISTRY);
        try {
            return new URL(pluginRegistryLoc);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public void setPluginRegistryLocation(URL url) {
        getPrefs().putString(PLUGIN_REGISTRY_KEY, url.toString());
        pluginRegistry = null;
    }


    public DownloadsProvider getPluginRegistry(){
        if (pluginRegistry == null){
            pluginRegistry = new RemotePluginRegistry(getPluginRegistryLocation());
        }
        return pluginRegistry;
    }


    public UpdatesProvider getUpdatesProvider() {
        if (updatesProvider == null){
            updatesProvider = new UpdatesProvider();
        }
        return updatesProvider;
    }


    public void checkForUpdates(){
        final BackgroundTask task = ProtegeApplication.getBackgroundTaskManager().startTask("searching for updates");
        Runnable runnable = new Runnable() {
            public void run() {
                UpdatesProvider updatesProvider = new UpdatesProvider();
                java.util.List<PluginInfo> updates = updatesProvider.getAvailableDownloads();
                ProtegeApplication.getBackgroundTaskManager().endTask(task);
                if (!updates.isEmpty()) {
                    Map<String, DownloadsProvider> map = new LinkedHashMap<String, DownloadsProvider>();
                    map.put("Updates", updatesProvider);
                    showUpdatesDialog(map);
                }
                else{
                    JOptionPane.showMessageDialog(null, "No updates available at this time.");
                }
            }
        };
        Thread t = new Thread(runnable, "Check for updates");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();

    }


    public void checkForDownloads() {
        final BackgroundTask task = ProtegeApplication.getBackgroundTaskManager().startTask("searching for downloads");

        Runnable runnable = new Runnable() {
            public void run() {
                DownloadsProvider registry = getPluginRegistry();
                final List<PluginInfo> downloads = registry.getAvailableDownloads();
                ProtegeApplication.getBackgroundTaskManager().endTask(task);
                if (!downloads.isEmpty()){
                    Map<String, DownloadsProvider> map = new LinkedHashMap<String, DownloadsProvider>();
                    map.put("Downloads", getPluginRegistry());
                    showUpdatesDialog(map);
                }
                else{
                    JOptionPane.showMessageDialog(null, "No downloads available at this time.");
                }
            }
        };
        Thread t = new Thread(runnable, "Check for downloads");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();

    }


    public void performAutoUpdate() {
        final BackgroundTask autoUpdateTask = ProtegeApplication.getBackgroundTaskManager().startTask("autoupdate");
        Runnable runnable = new Runnable() {
            public void run() {
                UpdatesProvider updatesProvider = new UpdatesProvider();
                List<PluginInfo> updates = updatesProvider.getAvailableDownloads();
                ProtegeApplication.getBackgroundTaskManager().endTask(autoUpdateTask);
                if (!updates.isEmpty()) {
                    Map<String, DownloadsProvider> map = new LinkedHashMap<String, DownloadsProvider>();
                    map.put("Updates", updatesProvider);
                    map.put("Downloads", new RemotePluginRegistry(getPluginRegistryLocation()));
                    showUpdatesDialog(map);
                }
            }
        };
        Thread t = new Thread(runnable, "Auto-update");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }


    public void performCheckPlugins() {
        final BackgroundTask autoUpdateTask = ProtegeApplication.getBackgroundTaskManager().startTask("searching for plugins");
        Runnable runnable = new Runnable() {
            public void run() {
                UpdatesProvider updatesProvider = new UpdatesProvider();
                List<PluginInfo> updates = updatesProvider.getAvailableDownloads();
                if (!updates.isEmpty()) {
                    ProtegeApplication.getBackgroundTaskManager().endTask(autoUpdateTask);
                    Map<String, DownloadsProvider> map = new LinkedHashMap<String, DownloadsProvider>();
                    map.put("Updates", updatesProvider);
                    map.put("Downloads", new RemotePluginRegistry(getPluginRegistryLocation()));
                    showUpdatesDialog(map);
                }
                else{
                    DownloadsProvider registry = getPluginRegistry();
                    final List<PluginInfo> downloads = registry.getAvailableDownloads();
                    ProtegeApplication.getBackgroundTaskManager().endTask(autoUpdateTask);
                    if (!downloads.isEmpty()){
                        Map<String, DownloadsProvider> map = new LinkedHashMap<String, DownloadsProvider>();
                        map.put("Downloads", registry);
                        map.put("Updates", updatesProvider);
                        showUpdatesDialog(map);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "No additional plugins / updates available at this time.");                        
                    }
                }
            }
        };
        Thread t = new Thread(runnable, "Check plugins");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }


    public void showUpdatesDialog(Map<String, DownloadsProvider> downloadsProviders) {
        List<PluginInfo> selUpdates = PluginPanel.showDialog(downloadsProviders, null);
        if (selUpdates != null){
            PluginInstaller installer = new PluginInstaller(selUpdates);
            installer.run();
            // @@TODO remove the installed plugins from the updatesProvider
        }
    }


    public void checkForUpdatesUI() {

    }
}
