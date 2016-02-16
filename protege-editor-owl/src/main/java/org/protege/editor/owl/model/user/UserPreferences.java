package org.protege.editor.owl.model.user;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23/11/15
 *
 * Preferences for the user.  For example, user name, ORCID etc.
 */
public class UserPreferences {

    public static Preferences get() {
        return PreferencesManager.getInstance().getApplicationPreferences("org.protege.user");
    }

}
