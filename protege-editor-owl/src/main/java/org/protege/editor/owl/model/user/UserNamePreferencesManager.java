package org.protege.editor.owl.model.user;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.protege.editor.core.prefs.Preferences;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23/11/15
 */
public class UserNamePreferencesManager {

    public static final String PREFERENCES_KEY = "user.name";

    private static final String USE_GIT_USER_NAME = "use.git.user.name";

    private final Preferences preferences;

    /**
     * Manages the preferences for a user.
     * @param preferences The actual preferences object for the user.  Not {@code null}.
     */
    public UserNamePreferencesManager(Preferences preferences) {
        this.preferences = checkNotNull(preferences);
    }

    /**
     * Determines whether or not the user name provided by Git (given the context of active ontologies that are under
     * Git version control) should be used as the user name.
     * @return true if the Git user name should be used when available or false if it should never be used.
     */
    public boolean isUseGitUserNameIfAvailable() {
        return preferences.getBoolean(USE_GIT_USER_NAME, false);
    }

    public void setUseGitUserNameIfAvailable(boolean b) {
        preferences.putBoolean(USE_GIT_USER_NAME, b);
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
