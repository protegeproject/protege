package org.protege.editor.core.editorkit;

import java.io.Serializable;
import java.net.URI;
import java.util.Properties;
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
 * Date: 25-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public final class EditorKitDescriptor implements Serializable {

    private Properties properties;

    public static final String LABEL_KEY = "EDITOR_KIT_LABEL";

    public static final String EDITOR_KIT_FACTORY_ID = "EDITOR_KIT_FACTORY_ID";


    public EditorKitDescriptor(String label, EditorKitFactory editorKitFactory) {
        this.properties = new Properties();
        setEditorKitFactoryID(editorKitFactory.getId());
        properties.setProperty(LABEL_KEY, label);
    }


    /**
     * Gets a human readable label that can be used
     * in user interfaces etc. to describe the clsdescriptioneditor kit.
     */
    public String getLabel() {
        return properties.getProperty(LABEL_KEY);
    }


    public String getEditorKitFactoryID() {
        return (String) properties.getProperty(EDITOR_KIT_FACTORY_ID);
    }


    public void setEditorKitFactoryID(String id) {
        properties.setProperty(EDITOR_KIT_FACTORY_ID, id);
    }


    public String getString(String key) {
        return (String) properties.get(key);
    }


    public void setString(String key, String value) {
        properties.put(key, value);
    }


    public void setURI(String key, URI uri) {
        properties.put(key, uri);
    }


    public URI getURI(String key) {
        return (URI) properties.get(key);
    }


    public boolean equals(Object obj) {
        if (obj instanceof EditorKitDescriptor) {
            return this.getLabel().equals(((EditorKitDescriptor) obj).getLabel());
        }
        else {
            return false;
        }
    }
}
