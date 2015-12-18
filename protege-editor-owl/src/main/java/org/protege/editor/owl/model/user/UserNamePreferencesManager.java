package org.protege.editor.owl.model.user;

import org.protege.editor.core.prefs.Preferences;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23/11/15
 */
public class UserNamePreferencesManager {

    public static final String PREFERENCES_KEY = "user.name";

    private final Preferences preferences;

    public UserNamePreferencesManager(Preferences preferences) {
        this.preferences = checkNotNull(preferences);
    }

    public Optional<String> getUserName() {
        return Optional.ofNullable(
                preferences.getString(PREFERENCES_KEY, null)
        );
    }

    public void setUserName(String userName) {
        preferences.putString(PREFERENCES_KEY, userName);
    }

    public void clearUserName() {
        preferences.putString(PREFERENCES_KEY, null);
    }



}
