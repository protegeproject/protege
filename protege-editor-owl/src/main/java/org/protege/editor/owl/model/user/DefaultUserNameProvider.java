package org.protege.editor.owl.model.user;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public class DefaultUserNameProvider implements UserNameProvider {

    @Override
    public Optional<String> getUserName() {
        String userName = System.getProperty("user.name");
        if(userName == null) {
            return Optional.empty();
        }
        return Optional.of(userName);
    }
}
