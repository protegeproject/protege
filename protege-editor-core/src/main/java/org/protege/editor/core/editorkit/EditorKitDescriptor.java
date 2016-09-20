package org.protege.editor.core.editorkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.net.URI;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public final class EditorKitDescriptor implements Serializable {

    public static final String LABEL_KEY = "EDITOR_KIT_LABEL";

    public static final String EDITOR_KIT_FACTORY_ID = "EDITOR_KIT_FACTORY_ID";


    private final Properties properties = new Properties();

    public EditorKitDescriptor(@Nonnull String label, @Nonnull EditorKitFactory editorKitFactory) {
        setEditorKitFactoryID(checkNotNull(editorKitFactory.getId()));
        properties.setProperty(LABEL_KEY, checkNotNull(label));
    }


    /**
     * Gets a human readable label that can be used
     * in user interfaces etc. to describe the clsdescriptioneditor kit.
     */
    @Nonnull
    public String getLabel() {
        return properties.getProperty(LABEL_KEY);
    }


    @Nonnull
    public String getEditorKitFactoryID() {
        return properties.getProperty(EDITOR_KIT_FACTORY_ID);
    }


    public void setEditorKitFactoryID(@Nonnull String id) {
        properties.setProperty(EDITOR_KIT_FACTORY_ID, checkNotNull(id));
    }


    @Nullable
    public String getString(@Nonnull String key) {
        return (String) properties.get(checkNotNull(key));
    }


    public void setString(@Nonnull String key, @Nonnull String value) {
        properties.put(key, value);
    }


    public void setURI(@Nonnull String key, @Nonnull URI uri) {
        properties.put(checkNotNull(key), checkNotNull(uri));
    }


    @Nullable
    public URI getURI(@Nonnull String key) {
        return (URI) properties.get(key);
    }


    public boolean equals(Object obj) {
        return obj instanceof EditorKitDescriptor && this.getLabel().equals(((EditorKitDescriptor) obj).getLabel());
    }


    @Nonnull
    public String toString() {
        return getLabel();
    }
}
