package org.protege.editor.core.prefs;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The preferences manager provides a global point for access to
 * application preferences and other sets of preferences.  Plugin
 * developers may use the preferences sets mechanisms to create and
 * store preferences for their own plugins.
 * <p/>
 * For plugins, typical use is to store preferences in a preference
 * set.  For example suppose a plugin stored data about the size of a
 * component - the xSize and the ySize.  To store these the plugin might
 * get hold of the <code>Preferences</code> object that stores these
 * particular preferences, for example, <code>getPreferencesForSet("org.mydomain.myplugin", "dimensions")</code>.
 * This would return the <code>Preferences</code> contained in the preference set "org.mydomain.myplugin" (i.e. the plugin's
 * set of preferences), which had the id "dimensions".  The plugin would then get hold of the xSize and the ySize attributes
 * using the method on the <code>Preferences</code> object.
 */
public abstract class PreferencesManager {


    private static PreferencesManager instance;


    /**
     * Gets the one an only instance of the <code>PreferencesManager</code>
     */
    public static synchronized PreferencesManager getInstance() {
        if (instance == null) {
            instance = new JavaBackedPreferencesManagerImpl();
        }
        return instance;
    }


    /**
     * Gets hold of the application preferences.  These preferences should in general
     * not be written to by plugins.
     * @param preferencesId The id of the particular application preferences to be
     *                      retrieved.
     */
    public abstract Preferences getApplicationPreferences(String preferencesId);


    /**
     * A convenience method that gets some application preferences with an id
     * that is generated from a class name.
     * @param c The name of the class that should be used to generate the id.
     */
    public abstract Preferences getApplicationPreferences(Class c);


    /**
     * Gets the preferences with the specified id, which are contained in the
     * preferences set with the specified id.
     * @param setId         The id of the preference set (typically used by plugins,
     *                      and corresponds to the plugin id).
     * @param preferencesId The id of the <code>Preferences</code> to be
     *                      retrieved from the preferences set.
     * @return The <code>Preferences</code> with the specfied <code>preferencesId</code>. This
     *         method will <b>not</b> return <code>null</code> - if the preferences don't exist they
     *         will be created.
     */
    public abstract Preferences getPreferencesForSet(String setId, String preferencesId);


    /**
     * A convenience method that gets preferences using an id generated from
     * the name of a class.
     * @param setId The id of the preferences set.
     * @param c     The class which will be used to generate the id of the <code>Preferences</code>
     * @return The <code>Preferences</code> with the generated id. This
     *         method will <b>not</b> return <code>null</code> - if the preferences don't exist they
     *         will be created.
     */
    public abstract Preferences getPreferencesForSet(String setId, Class c);


    /**
     * Resets the preferences to the factory settings.  In most cases this will simply wipe out all preferences.
     */
    public abstract void resetPreferencesToFactorySettings();
}
