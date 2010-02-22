package org.protege.editor.core.prefs;
/*
 * Copyright (C) 2008, University of Manchester
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
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 12-Aug-2008<br><br>
 */
public class JavaBackedPreferencesManagerImpl extends PreferencesManager {

    private static final String APPLICATION_PREFERENCES = "application_preferences";

    @SuppressWarnings("unchecked")
    public Preferences getApplicationPreferences(Class c) {
        return new JavaBackedPreferencesImpl(APPLICATION_PREFERENCES, c.getName());
    }


    public Preferences getApplicationPreferences(String preferencesId) {
        return new JavaBackedPreferencesImpl(APPLICATION_PREFERENCES, preferencesId);
    }


    @SuppressWarnings("unchecked")
    public Preferences getPreferencesForSet(String setId, Class c) {
        return new JavaBackedPreferencesImpl(setId, c.getName());
    }


    public Preferences getPreferencesForSet(String setId, String preferencesId) {
        return new JavaBackedPreferencesImpl(setId, preferencesId);
    }
}
