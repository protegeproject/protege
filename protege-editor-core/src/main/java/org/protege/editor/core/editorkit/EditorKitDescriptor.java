package org.protege.editor.core.editorkit;

import java.io.Serializable;
import java.net.URI;
import java.util.Properties;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-May-2006<br><br>

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


    public String toString() {
        return getLabel();
    }
}
