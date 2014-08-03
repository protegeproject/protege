package org.protege.editor.owl.model;

import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.XMLWriterPreferences;
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
