package org.protege.editor.owl.model.user;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.Properties;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public class DefaultUserNameProvider implements UserNameProvider {

    public static final String SYSTEM_USER_NAME_PROPERTY = "user.name";


    private final UserNameProvider gitRepositoryUserNameProvider;

    private final UserNamePreferencesManager preferencesManager;

    private final Properties properties;


    /**
     * Creates a user name provide that will obtained details from a UserNamePreferencesManager in the first
     * instances and failing that a properties object (usually System.getProperties()).
     * @param gitRepositoryUserNameProvider A Git repository manager that can provide a user name.
     * @param preferencesManager The UserNamePreferencesManager. Not {@code null}.
     * @param properties The Properties.  Usually System.getProperties().  Not {@code null}.  Note that this object
     *                   is not copied, so changes in the object could cause in changes to the result returned by the
     *                   getUserName() method.
     */
    public DefaultUserNameProvider(@Nonnull UserNameProvider gitRepositoryUserNameProvider,
                                   @Nonnull UserNamePreferencesManager preferencesManager,
                                   @Nonnull Properties properties) {
        this.gitRepositoryUserNameProvider = checkNotNull(gitRepositoryUserNameProvider);
        this.preferencesManager = checkNotNull(preferencesManager);
        this.properties = checkNotNull(properties);
    }

    @Override
    public Optional<String> getUserName() {
        Optional<String> gitUserName = getGitUserNameIfAvailable();
        if(gitUserName.isPresent()) {
            return gitUserName;
        }
        Optional<String> userName = preferencesManager.getUserName();
        if(userName.isPresent()) {
            return userName;
        }
        return Optional.ofNullable(properties.getProperty(SYSTEM_USER_NAME_PROPERTY));
    }

    private Optional<String> getGitUserNameIfAvailable() {
        if(preferencesManager.isUseGitUserNameIfAvailable()) {
            return gitRepositoryUserNameProvider.getUserName();
        }
        else {
            return Optional.empty();
        }
    }
}
