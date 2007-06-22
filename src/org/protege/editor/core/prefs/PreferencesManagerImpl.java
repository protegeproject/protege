package org.protege.editor.core.prefs;

import org.apache.log4j.Logger;
import org.protege.editor.core.FileManager;

import java.io.*;
import java.util.Map;
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
 * Date: 20-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesManagerImpl extends AbstractPreferencesManager {

    private static final Logger logger = Logger.getLogger(PreferencesManagerImpl.class);

//    private Map<String, Preferences> applicationPrefs;
//
//    private Map<String, Map<String, Preferences>> projectPrefs;

    private static final String PREFERENCES_FOLDER_NAME = "Preferences";

    private static final String PREFERENCE_SETS_FOLDER_NAME = "PreferenceSets";

    private static final String APP_PREFERENCES_FILE_NAME = "ApplicationPreferences";

    private static final String PREFERENCES_SET_FILE_PREFIX = "PreferenceSet_";


    public PreferencesManagerImpl() {
//        this.applicationPrefs = new HashMap<String, Preferences>();
//        this.projectPrefs = new HashMap<String, Map<String, Preferences>>();
        load();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                save();
            }
        }));
    }


    private static File getPreferencesFolder() {
        File dataFolder = FileManager.getDataFolder();
        File preferencesFolder = new File(dataFolder, PREFERENCES_FOLDER_NAME);
        if (!preferencesFolder.exists()) {
            preferencesFolder.mkdir();
        }
        return preferencesFolder;
    }


    protected void load() {
        try {
            File prefsFolder = getPreferencesFolder();
            File appsPrefsFile = new File(prefsFolder, APP_PREFERENCES_FILE_NAME);
            if (appsPrefsFile.exists()) {
                ObjectInputStream ois = createObjectInputStream(appsPrefsFile);
                Map<String, Preferences> applicationPrefs = (Map<String, Preferences>) ois.readObject();
                ois.close();
                for (String id : applicationPrefs.keySet()) {
                    addApplicationPreferences(id, applicationPrefs.get(id));
                }
            }
            File projectPrefsFolder = new File(prefsFolder, PREFERENCE_SETS_FOLDER_NAME);
            if (!projectPrefsFolder.exists()) {
                return;
            }
            for (File curFile : projectPrefsFolder.listFiles()) {
                if (curFile.isDirectory()) {
                    continue;
                }
                if (curFile.getName().startsWith(PREFERENCES_SET_FILE_PREFIX)) {
                    ObjectInputStream ois = createObjectInputStream(curFile);
                    // Read the preferences set key, followed by the preferences
                    // map.
                    String key = (String) ois.readObject();
                    Map<String, Preferences> map = (Map<String, Preferences>) ois.readObject();
                    for (String id : map.keySet()) {
                        addPreferencesSet(key, id, map.get(id));
                    }
                    ois.close();
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
            File prefsFolder = getPreferencesFolder();
            if (!prefsFolder.exists()) {
                prefsFolder.mkdir();
            }
            File appPrefsFile = new File(prefsFolder, APP_PREFERENCES_FILE_NAME);
            if (!appPrefsFile.exists()) {
                appPrefsFile.createNewFile();
            }
            ObjectOutputStream oos = createObjectOutputStream(appPrefsFile);
            oos.writeObject(getApplicationPrefs());
            oos.flush();
            oos.close();

            File projectPrefsFolder = new File(prefsFolder, PREFERENCE_SETS_FOLDER_NAME);
            if (!projectPrefsFolder.exists()) {
                projectPrefsFolder.mkdir();
            }
            Map<String, Map<String, Preferences>> prefSets = getProjectPrefs();
            for (String key : prefSets.keySet()) {
                File curPrefsFile = new File(projectPrefsFolder, PREFERENCES_SET_FILE_PREFIX + key);
                if (!curPrefsFile.exists()) {
                    curPrefsFile.createNewFile();
                }
                ObjectOutputStream poos = createObjectOutputStream(curPrefsFile);
                poos.writeObject(key);
                poos.writeObject(prefSets.get(key));
                poos.flush();
                poos.close();
            }
        }
        catch (IOException e) {
            logger.error(e);
        }
    }


    private static ObjectOutputStream createObjectOutputStream(File f) throws FileNotFoundException, IOException {
        return new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
    }


    private static ObjectInputStream createObjectInputStream(File f) throws IOException {
        return new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
    }

//    public Preferences getApplicationPreferences(String id) {
//        Preferences p = applicationPrefs.get(id);
//        if (p == null) {
//            p = new PreferencesImpl();
//            applicationPrefs.put(id, p);
//        }
//        return p;
//    }
//
//    public Preferences getApplicationPreferences(Class c) {
//        return getApplicationPreferences(c.getName());
//    }
//
//    public Preferences getPreferencesForSet(String preferencesSetId, String prefId) {
//        Map<String, Preferences> map = projectPrefs.get(preferencesSetId);
//        if (map == null) {
//            map = new HashMap<String, Preferences>();
//            projectPrefs.put(preferencesSetId, map);
//        }
//        Preferences p = map.get(prefId);
//        if (p == null) {
//            p = new PreferencesImpl();
//            map.put(prefId, p);
//        }
//        return p;
//    }
//
//    public Preferences getPreferencesForSet(String projId, Class c) {
//        return getPreferencesForSet(projId, c.getName());
//    }

}
