package org.protege.editor.owl.model.hierarchy;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Aug 2017
 */
public class ClassHierarchyPreferences {

    private static final String CLASS_HIERARCHY_PREFERENCES = "CLASS_HIERARCHY_PREFERENCES";

    private static final ClassHierarchyPreferences PREFERENCES = new ClassHierarchyPreferences();

    private static final String DISPLAY_RELATIONSHIPS_KEY = "DISPLAY_RELATIONSHIPS";

    private static Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(CLASS_HIERARCHY_PREFERENCES);
    }

    public static ClassHierarchyPreferences get() {
        return PREFERENCES;
    }

    public boolean isDisplayRelationships() {
        return getPreferences().getBoolean(DISPLAY_RELATIONSHIPS_KEY, false);
    }

    public void setDisplayRelationships(boolean displayRelationships) {
        getPreferences().putBoolean(DISPLAY_RELATIONSHIPS_KEY, displayRelationships);
    }
}
