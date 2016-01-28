package org.protege.editor.owl.model.annotation;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 27/01/16
 */
public class EntityCreationMetadataPreferences {

    private EntityCreationMetadataPreferences() {
    }

    public static Preferences get() {
        return PreferencesManager.getInstance().getApplicationPreferences(EntityCreationMetadataPreferences.class);
    }
}
