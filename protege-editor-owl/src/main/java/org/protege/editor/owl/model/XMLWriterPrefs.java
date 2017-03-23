package org.protege.editor.owl.model;

import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;
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

    public void setUseEntities(OWLOntologyManager manager, boolean b) {
        PreferencesManager.getInstance().getApplicationPreferences(KEY).putBoolean(USE_ENTITIES, b);
        manager.setOntologyConfigurator(manager.getOntologyConfigurator().withUseNamespaceEntities(b));
    }
}
