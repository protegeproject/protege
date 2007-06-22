package org.protege.editor.core.prefs;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
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
 * Date: 02-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An implementation of the preferences manager that stores preferences
 * using the Java Preferences API.  This means that preferences are persisted
 * regardless of the current installation of protege.
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
