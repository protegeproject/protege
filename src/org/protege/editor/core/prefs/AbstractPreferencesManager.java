package org.protege.editor.core.prefs;
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


import java.util.HashMap;
import java.util.Map;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractPreferencesManager extends PreferencesManager {

    private Map<String, Preferences> applicationPrefs;

    private Map<String, Map<String, Preferences>> projectPrefs;


    public AbstractPreferencesManager() {
        this.applicationPrefs = new HashMap<String, Preferences>();
        this.projectPrefs = new HashMap<String, Map<String, Preferences>>();
        // Load the preferences
        load();
        // Add in a shutdown hook to save the preferences
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                save();
            }
        }));
    }


    protected abstract void load();


    protected abstract void save();


    protected Map<String, Preferences> getApplicationPrefs() {
        return applicationPrefs;
    }


    public Map<String, Map<String, Preferences>> getProjectPrefs() {
        return projectPrefs;
    }


    protected void addApplicationPreferences(String id, Preferences p) {
        applicationPrefs.put(id, p);
    }


    protected void addPreferencesSet(String setId, String prefsId, Preferences p) {
        Map<String, Preferences> map = projectPrefs.get(setId);
        if (map == null) {
            map = new HashMap<String, Preferences>();
            projectPrefs.put(setId, map);
        }
        // Set the preferences
        map.put(prefsId, p);
    }


    public Preferences getApplicationPreferences(String id) {
        Preferences p = applicationPrefs.get(id);
        if (p == null) {
            p = new PreferencesImpl();
            applicationPrefs.put(id, p);
        }
        return p;
    }


    public Preferences getApplicationPreferences(Class c) {
        return getApplicationPreferences(c.getName());
    }


    public Preferences getPreferencesForSet(String preferencesSetId, String prefId) {
        Map<String, Preferences> map = projectPrefs.get(preferencesSetId);
        if (map == null) {
            map = new HashMap<String, Preferences>();
            projectPrefs.put(preferencesSetId, map);
        }
        Preferences p = map.get(prefId);
        if (p == null) {
            p = new PreferencesImpl();
            map.put(prefId, p);
        }
        return p;
    }


    public Preferences getPreferencesForSet(String projId, Class c) {
        return getPreferencesForSet(projId, c.getName());
    }
}
