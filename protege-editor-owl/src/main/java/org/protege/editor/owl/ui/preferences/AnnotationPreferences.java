package org.protege.editor.owl.ui.preferences;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 14-Aug-2007<br><br>
 */
public class AnnotationPreferences {

    public static final String PREFERENCES_SET_KEY = "ANNOTATION_PREFS_SET";

    public static final String HIDDEN_URIS_KEY = "HIDDEN_ANNOATIONS_URIS";


    public static Set<URI> getHiddenAnnotationURIs() {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        Preferences prefs = prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, HIDDEN_URIS_KEY);
        Set<URI> uris = new HashSet<>();
        for (String s : prefs.getStringList(HIDDEN_URIS_KEY, new ArrayList<>())) {
            uris.add(URI.create(s));
        }
        return uris;
    }


    public static void setHiddenAnnotationURIs(Set<URI> uris) {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        Preferences prefs = prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, HIDDEN_URIS_KEY);
        List<String> list = new ArrayList<>();
        for (URI uri : uris) {
            list.add(uri.toString());
        }
        prefs.putStringList(HIDDEN_URIS_KEY, list);
    }
}
