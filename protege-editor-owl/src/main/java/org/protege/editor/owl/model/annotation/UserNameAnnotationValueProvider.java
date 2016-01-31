package org.protege.editor.owl.model.annotation;

import org.protege.editor.owl.model.user.UserNameProvider;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public class UserNameAnnotationValueProvider implements AnnotationValueProvider {

    private final UserNameProvider userNameProvider;

    public UserNameAnnotationValueProvider(UserNameProvider userNameProvider) {
        this.userNameProvider = userNameProvider;
    }

    @Override
    public Optional<OWLAnnotationValue> getAnnotationValue(OWLDataFactory dataFactory) {
        Optional<String> userName = userNameProvider.getUserName();
        if(userName.isPresent()) {
            return Optional.of(dataFactory.getOWLLiteral(userName.get()));
        }
        else {
            return Optional.empty();
        }
    }
}
