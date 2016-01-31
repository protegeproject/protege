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

    /**
     * Manages the preferences for a user.
     * @param preferences The actual preferences object for the user.  Not {@code null}.
     */
    public UserNamePreferencesManager(Preferences preferences) {
        this.preferences = checkNotNull(preferences);
    }

    public Optional<String> getUserName() {
        return Optional.ofNullable(
                preferences.getString(PREFERENCES_KEY, null)
        );
    }

    /**
     * Sets the user name.
     * @param userName The user name.  Not {@code null}.
     * @throws java.lang.NullPointerException if the user name is {@code null}.
     */
    public void setUserName(String userName) {
        preferences.putString(PREFERENCES_KEY, checkNotNull(userName));
    }

    public void clearUserName() {
        preferences.putString(PREFERENCES_KEY, null);
    }



}
