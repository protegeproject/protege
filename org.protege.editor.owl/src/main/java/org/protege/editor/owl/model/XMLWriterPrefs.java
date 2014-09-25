package org.protege.editor.owl.model;

import org.protege.editor.core.prefs.PreferencesManager;
import org.coode.xml.XMLWriterPreferences;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Nov-2007<br><br>
 */
public class XMLWriterPrefs {

    private static XMLWriterPrefs instance;

    private static final String KEY = "XMLWriterPrefs";

    private static final String USE_ENTITIES = "UseEntities";

    public static synchronized XMLWriterPrefs getInstance() {
        if(instance == null) {
            instance = new XMLWriterPrefs();
        }
        return instance;
    }

    public boolean isUseEntities() {
        return PreferencesManager.getInstance().getApplicationPreferences(KEY).getBoolean(USE_ENTITIES, true);
    }

    public void setUseEntities(boolean b) {
        PreferencesManager.getInstance().getApplicationPreferences(KEY).putBoolean(USE_ENTITIES, b);
        XMLWriterPreferences.getInstance().setUseNamespaceEntities(b);
    }
}
