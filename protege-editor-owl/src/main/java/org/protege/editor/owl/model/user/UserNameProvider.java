package org.protege.editor.owl.model.user;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public interface UserNameProvider {

    Optional<String> getUserName();
}
