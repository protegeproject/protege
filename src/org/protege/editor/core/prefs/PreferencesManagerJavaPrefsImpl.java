package org.protege.editor.core.prefs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An implementation of the preferences manager that stores preferences
 * using the Java Preferences API.  This means that preferences are persisted
 * regardless of the current installation of protege.
 *
 * @deprecated JavaBackedPreferencesManagerImpl should be used.
 */
public class PreferencesManagerJavaPrefsImpl extends AbstractPreferencesManager {

    private static final Logger logger = Logger.getLogger(PreferencesManagerJavaPrefsImpl.class);

    public static final String PROTEGE_PREFERENCES_BASE = "org.protege.preferences";

    public static final String APPLICATION_PREFERENCES_KEY = PROTEGE_PREFERENCES_BASE + ".application";


    public static final String PREFERENCE_SETS_KEY = PROTEGE_PREFERENCES_BASE + "preferencesetids";


    public PreferencesManagerJavaPrefsImpl() {
        super();
    }


    protected void load() {
        // Load application preferences
        try {
            Preferences userRoot = Preferences.userRoot();
            byte [] appBytes = userRoot.getByteArray(APPLICATION_PREFERENCES_KEY, null);
            if (appBytes != null) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(appBytes));
                // Read in our application preferences
                Map<String, org.protege.editor.core.prefs.Preferences> applicationPrefs = (Map<String, org.protege.editor.core.prefs.Preferences>) ois.readObject();
                ois.close();
                // Add the preferences
                for (String id : applicationPrefs.keySet()) {
                    addApplicationPreferences(id, applicationPrefs.get(id));
                }
            }

            byte [] prefsSetBytes = userRoot.getByteArray(PREFERENCE_SETS_KEY, null);
            if (prefsSetBytes != null) {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(prefsSetBytes));
                Map<String, Map<String, org.protege.editor.core.prefs.Preferences>> preferenceSetsMap = (Map<String, Map<String, org.protege.editor.core.prefs.Preferences>>) ois.readObject();
                // Plonk the preferences in
                for (String prefsSetId : preferenceSetsMap.keySet()) {
                    Map<String, org.protege.editor.core.prefs.Preferences> setPrefs = preferenceSetsMap.get(prefsSetId);
                    for (String prefsId : setPrefs.keySet()) {
                        addPreferencesSet(prefsSetId, prefsId, setPrefs.get(prefsId));
                    }
                }
            }
        }
        catch (IOException e) {
            logger.error(e);
        }
        catch (ClassNotFoundException e) {
            logger.error(e);
        }
    }


    protected void save() {
        try {
            Preferences userRoot = Preferences.userRoot();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream appPrefsOOS = new ObjectOutputStream(bos);
            // Write out the app preferences
            appPrefsOOS.writeObject(getApplicationPrefs());
            appPrefsOOS.flush();
            appPrefsOOS.close();
            // Store the byte array in the Java prefs system
            userRoot.putByteArray(APPLICATION_PREFERENCES_KEY, bos.toByteArray());

            // Now for the preference sets
            ByteArrayOutputStream prefsSetBOS = new ByteArrayOutputStream();
            ObjectOutputStream prefsSetOOS = new ObjectOutputStream(prefsSetBOS);
            prefsSetOOS.writeObject(getProjectPrefs());
            prefsSetOOS.flush();
            prefsSetOOS.close();
            userRoot.putByteArray(PREFERENCE_SETS_KEY, prefsSetBOS.toByteArray());
            userRoot.flush();
        }
        catch (IOException e) {
            logger.error(e);
        }
        catch (BackingStoreException e) {
            logger.error(e);
        }
    }
}
