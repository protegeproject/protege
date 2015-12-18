package org.protege.editor.owl.model.user;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public class DefaultUserNameProvider implements UserNameProvider {

    private final UserNamePreferencesManager preferencesManager;

    public DefaultUserNameProvider(UserNamePreferencesManager preferencesManager) {
        this.preferencesManager = checkNotNull(preferencesManager);
    }

    @Override
    public Optional<String> getUserName() {
        Optional<String> userName = preferencesManager.getUserName();
        if(userName.isPresent()) {
            return userName;
        }
        String loggedInUserName = System.getProperty("user.name");
        if(loggedInUserName == null) {
            return Optional.empty();
        }
        return Optional.of(loggedInUserName);
    }
}
