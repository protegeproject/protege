package org.protege.editor.core.update;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UpdateManager {

    private static UpdateManager instance;

    private static final Logger logger = Logger.getLogger(UpdateManager.class);

    public static final String PREFERENCES_KEY = "CheckForUpdates";
    public static final String UPDATE_URL = "Update-Url";


    private UpdateManager() {

    }


    public static synchronized UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }


    public void setAutoUpdateEnabled(boolean b) {
        PreferencesManager man = PreferencesManager.getInstance();
        Preferences prefs = man.getApplicationPreferences(UpdateManager.class);
        prefs.putBoolean(PREFERENCES_KEY, b);
    }


    public boolean isAutoUpdateEnabled() {
        PreferencesManager man = PreferencesManager.getInstance();
        Preferences prefs = man.getApplicationPreferences(UpdateManager.class);
        return prefs.getBoolean(PREFERENCES_KEY, true);
    }


    public void checkForUpdates(boolean overridePreferences) {
        if (!overridePreferences && !isAutoUpdateEnabled()) {
            return;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                List<UpdateInfo> updates = new ArrayList<UpdateInfo>();
                BundleContext context = PluginUtilities.getInstance().getApplicationContext();
                for (final Bundle b : context.getBundles()) {
                    try {
                        String url = (String) b.getHeaders().get(UPDATE_URL);
                        if (url != null) {
                            final URI updateFileURI = new URI(url);
                            UpdateChecker checker = new UpdateChecker(updateFileURI, b);
                            UpdateInfo info = checker.run();
                            if (info != null) {
                                updates.add(info);
                            }
                        }
                    }
                    catch (Exception e) {
                        logger.warn(e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
                if (!updates.isEmpty()) {
                    showUpdatesDialog(updates);
                }
            }
        };
        Thread t = new Thread(runnable);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }


    private void showUpdatesDialog(List<UpdateInfo> updates) {
        List<UpdateInfo> selUpdates = PluginUpdatePanel.showDialog(updates);
        UpdateInstaller installer = new UpdateInstaller(selUpdates);
        installer.run();
    }


    public void checkForUpdatesUI() {

    }
}
