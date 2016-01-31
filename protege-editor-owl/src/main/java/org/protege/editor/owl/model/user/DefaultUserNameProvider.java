package org.protege.editor.owl.model.user;

import java.util.Optional;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public class DefaultUserNameProvider implements UserNameProvider {

    public static final String SYSTEM_USER_NAME_PROPERTY = "user.name";

    private final UserNamePreferencesManager preferencesManager;

    private final Properties properties;

    /**
     * Creates a user name provide that will obtained details from a UserNamePreferencesManager in the first
     * instances and failing that a properties object (usually System.getProperties()).
     * @param preferencesManager The UserNamePreferencesManager. Not {@code null}.
     * @param properties The Properties.  Usually System.getProperties().  Not {@code null}.  Note that this object
     *                   is not copied, so changes in the object could cause in changes to the result returned by the
     *                   getUserName() method.
     */
    public DefaultUserNameProvider(UserNamePreferencesManager preferencesManager, Properties properties) {
        this.preferencesManager = checkNotNull(preferencesManager);
        this.properties = checkNotNull(properties);
    }

    @Override
    public Optional<String> getUserName() {
        Optional<String> userName = preferencesManager.getUserName();
        if(userName.isPresent()) {
            return userName;
        }
        return Optional.ofNullable(properties.getProperty(SYSTEM_USER_NAME_PROPERTY));
    }
}
