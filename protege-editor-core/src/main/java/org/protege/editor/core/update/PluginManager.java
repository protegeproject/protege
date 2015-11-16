package org.protege.editor.core.update;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.progress.BackgroundTask;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.protege.editor.core.update.PluginRegistryImpl.PluginRegistryType.PLUGIN_DOWNLOAD_REGISTRY;
import static org.protege.editor.core.update.PluginRegistryImpl.PluginRegistryType.PLUGIN_UPDATE_REGISTRY;

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

    private static final String LAST_RUN_PREFS_KEY = "last.run";

    private static PluginManager instance;

    public static final String AUTO_UPDATE_KEY = "CheckForUpdates";

    public static final String PLUGIN_REGISTRY_KEY = "plugin.registry-5.0.url";
    public static final String DEFAULT_REGISTRY = "https://raw.githubusercontent.com/protegeproject/autoupdate/master/plugins.repository";

    private PluginRegistry pluginRegistry;

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
    	String oldPluginRegistryLoc = getPrefs().getString(PLUGIN_REGISTRY_KEY, DEFAULT_REGISTRY);
    	String newPluginRegistryLoc = url.toString();
    	if (!newPluginRegistryLoc.equals(oldPluginRegistryLoc)) {
    		getPrefs().putString(PLUGIN_REGISTRY_KEY, newPluginRegistryLoc);
    		pluginRegistry = null;
    	}
    }


    public PluginRegistry getPluginRegistry(){
        if (pluginRegistry == null){
            pluginRegistry = new PluginRegistryImpl(getPluginRegistryLocation(), PLUGIN_DOWNLOAD_REGISTRY);
        }
        return pluginRegistry;
    }


    public void checkForUpdates(){
        final BackgroundTask task = ProtegeApplication.getBackgroundTaskManager().startTask("searching for updates");
        Runnable runnable = () -> {
            try {
                PluginRegistry updatesProvider = new PluginRegistryImpl(getPluginRegistryLocation(), PLUGIN_UPDATE_REGISTRY);
                List<PluginInfo> updates = updatesProvider.getAvailableDownloads();
                if (!updates.isEmpty()) {
                    Map<String, PluginRegistry> map = new LinkedHashMap<>();
                    map.put("Updates", updatesProvider);
                    SwingUtilities.invokeLater(
                            () -> showUpdatesDialog(map)
                    );
                }
                else{
                    SwingUtilities.invokeLater(
                            () -> JOptionPane.showMessageDialog(null, "No updates available at this time.")
                    );
                }
            }
            finally {
                SwingUtilities.invokeLater(
                        () -> ProtegeApplication.getBackgroundTaskManager().endTask(task)
                );
            }

        };
        Thread t = new Thread(runnable, "Auto-update Runner");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();

    }


    public void checkForDownloads() {
        final BackgroundTask task = ProtegeApplication.getBackgroundTaskManager().startTask("searching for downloads");

        Runnable runnable = () -> {
            List<PluginInfo> downloads;
            try {
                PluginRegistry registry = getPluginRegistry();
                downloads = registry.getAvailableDownloads();
            }
            finally {
                ProtegeApplication.getBackgroundTaskManager().endTask(task);
            }
            if (!downloads.isEmpty()){
                Map<String, PluginRegistry> map = new LinkedHashMap<>();
                map.put("Downloads", getPluginRegistry());
                showUpdatesDialog(map);
            }
            else{
                JOptionPane.showMessageDialog(null, "No downloads available at this time.");
            }
        };
        Thread t = new Thread(runnable, "Check for downloads");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();

    }

    /**
     * Gets the date that auto-update was last run.
     * @return The date which auto-update was last run.  Not {@code null}.
     */
    public Date getLastAutoUpdateDate() {
//        Calendar c = Calendar.getInstance();
//        c.set(Calendar.DAY_OF_MONTH, 20);
//        getPrefs().putLong(LAST_RUN_PREFS_KEY, c.getTimeInMillis());
        long lastRun = getPrefs().getLong(LAST_RUN_PREFS_KEY, 0);
        return new Date(lastRun);
    }

    public void performAutoUpdate() {

        final BackgroundTask autoUpdateTask = ProtegeApplication.getBackgroundTaskManager().startTask("autoupdate");
        Runnable runnable = new Runnable() {
        	PluginRegistry updatesProvider;
        	List<PluginInfo> updates;
            public void run() {
            	try {
            		updatesProvider = new PluginRegistryImpl(getPluginRegistryLocation(), PLUGIN_UPDATE_REGISTRY);
            		updates = updatesProvider.getAvailableDownloads();
                    getPrefs().putLong(LAST_RUN_PREFS_KEY, System.currentTimeMillis());
                }
            	finally {
            		ProtegeApplication.getBackgroundTaskManager().endTask(autoUpdateTask);
            	}
                if (!updates.isEmpty()) {
                    Map<String, PluginRegistry> map = new LinkedHashMap<String, PluginRegistry>();
                    map.put(PLUGIN_UPDATE_REGISTRY.getLabel(), updatesProvider);
                    map.put(PLUGIN_DOWNLOAD_REGISTRY.getLabel(), new PluginRegistryImpl(getPluginRegistryLocation(), PLUGIN_DOWNLOAD_REGISTRY));
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
        Runnable runnable = () -> {
            try {
                PluginRegistry updatesProvider = new PluginRegistryImpl(getPluginRegistryLocation(), PLUGIN_UPDATE_REGISTRY);
                List<PluginInfo> updates = updatesProvider.getAvailableDownloads();
                if (!updates.isEmpty()) {
                    Map<String, PluginRegistry> map = new LinkedHashMap<>();
                    map.put(PLUGIN_UPDATE_REGISTRY.getLabel(), updatesProvider);
                    map.put(PLUGIN_DOWNLOAD_REGISTRY.getLabel(), new PluginRegistryImpl(getPluginRegistryLocation(), PLUGIN_DOWNLOAD_REGISTRY));
                    showUpdatesDialog(map);
                }
                else{
                    PluginRegistry registry = getPluginRegistry();
                    final List<PluginInfo> downloads = registry.getAvailableDownloads();
                    if (!downloads.isEmpty()){
                        Map<String, PluginRegistry> map = new LinkedHashMap<>();
                        map.put("Downloads", registry);
                        map.put("Updates", updatesProvider);
                        showUpdatesDialog(map);
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "No additional plugins / updates available at this time.");
                    }
                }
            }
            finally {
                ProtegeApplication.getBackgroundTaskManager().endTask(autoUpdateTask);
            }
        };
        Thread t = new Thread(runnable, "Check plugins");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }


    public void showUpdatesDialog(Map<String, PluginRegistry> downloadsProviders) {
        List<PluginInfo> selUpdates = PluginPanel.showDialog(downloadsProviders, null);
        if (!selUpdates.isEmpty()){
            PluginInstaller installer = new PluginInstaller(selUpdates);
            installer.run();
            // @@TODO remove the installed plugins from the updatesProvider
        }
    }


    public void checkForUpdatesUI() {

    }
}
