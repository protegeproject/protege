package org.protege.editor.core.prefs;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;

/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 12-Aug-2008<br><br>
 */
public class JavaBackedPreferencesManagerImpl extends PreferencesManager {

    private static final String APPLICATION_PREFERENCES = "application_preferences";

    private Logger logger = LoggerFactory.getLogger(JavaBackedPreferencesManagerImpl.class);

    @SuppressWarnings("rawtypes")
    public Preferences getApplicationPreferences(Class c) {
        return new JavaBackedPreferencesImpl(APPLICATION_PREFERENCES, c.getName());
    }


    public Preferences getApplicationPreferences(String preferencesId) {
        return new JavaBackedPreferencesImpl(APPLICATION_PREFERENCES, preferencesId);
    }


    @SuppressWarnings({"rawtypes"})
    public Preferences getPreferencesForSet(String setId, Class c) {
        return new JavaBackedPreferencesImpl(setId, c.getName());
    }


    public Preferences getPreferencesForSet(String setId, String preferencesId) {
        return new JavaBackedPreferencesImpl(setId, preferencesId);
    }

    /**
     * Resets the preferences to the factory settings.  In most cases this will simply wipe out all preferences.
     */
    @Override
    public void resetPreferencesToFactorySettings() {
        try {
            java.util.prefs.Preferences userRoot = java.util.prefs.Preferences.userRoot();
            java.util.prefs.Preferences protegePreferencesRoot = userRoot.node(JavaBackedPreferencesImpl.PROTEGE_PREFS_KEY);
            protegePreferencesRoot.removeNode();


        }
        catch (BackingStoreException e) {
            logger.error("An error occurred whilst clearing the preferences: {}", e);
        }
    }

}
