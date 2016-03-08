package org.protege.editor.core.update;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.editorkit.EditorKitManager;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.progress.BackgroundTask;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.core.ui.workspace.WorkspaceManager;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

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

    private static final PluginManager instance = new PluginManager();

    public static final String AUTO_UPDATE_KEY = "CheckForUpdates";

    public static final String PLUGIN_REGISTRY_KEY = "plugin.registry-5.0.url";

    public static final String DEFAULT_REGISTRY = "https://raw.githubusercontent.com/protegeproject/autoupdate/master/update-info/5.0.0/plugins.repository";

    private static enum SearchType {
        UPDATES_ONLY,
        UPDATES_AND_INSTALLS
    }

    private PluginManager() {

    }


    public static synchronized PluginManager getInstance() {
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
    	}
    }

    /**
     * Gets the date that auto-update was last run.
     * @return The date which auto-update was last run.  Not {@code null}.
     */
    public Date getLastAutoUpdateDate() {
        long lastRun = getPrefs().getLong(LAST_RUN_PREFS_KEY, 0);
        return new Date(lastRun);
    }

    public void runAutoUpdate() {
        runSearch(SearchType.UPDATES_ONLY);
    }

    public void runCheckForPlugins() {
        runSearch(SearchType.UPDATES_AND_INSTALLS);
    }

    private void runSearch(SearchType searchType) {
        final BackgroundTask autoUpdateTask = ProtegeApplication.getBackgroundTaskManager().startTask("autoupdate");
        Runnable runnable = () -> {
            PluginRegistry updatesProvider = new PluginRegistryImpl(getPluginRegistryLocation());
            try {
                updatesProvider.reload();
                getPrefs().putLong(LAST_RUN_PREFS_KEY, System.currentTimeMillis());
            }
            finally {
                ProtegeApplication.getBackgroundTaskManager().endTask(autoUpdateTask);
                ListMultimap<String, PluginInfo> map = ArrayListMultimap.create();
                List<PluginInfo> availableUpdates = updatesProvider.getAvailableUpdates();
                map.putAll(PLUGIN_UPDATE_REGISTRY.getLabel(), availableUpdates);
                map.putAll(PLUGIN_DOWNLOAD_REGISTRY.getLabel(), updatesProvider.getAvailableInstalls());
                if (searchType == SearchType.UPDATES_ONLY) {
                    if (!availableUpdates.isEmpty()) {
                        showUpdatesDialog(map);
                    }
                }
                if(searchType == SearchType.UPDATES_AND_INSTALLS) {
                    showUpdatesDialog(map);
                }
            }
        };
        Thread t = new Thread(runnable, "Auto-Update");
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    private void showUpdatesDialog(ListMultimap<String, PluginInfo> downloadsProviders) {
       SwingUtilities.invokeLater(() -> {
           List<PluginInfo> selUpdates = PluginPanel.showDialog(downloadsProviders, null);
           if (!selUpdates.isEmpty()){
               PluginInstaller installer = new PluginInstaller(selUpdates);
               installer.run();
           }
       });
    }

}
