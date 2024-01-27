package org.protege.editor.owl.model.annotation;

import javax.inject.Inject;
import javax.inject.Provider;

import org.semanticweb.owlapi.model.IRI;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 27/01/16
 */
public class DefaultUserNameAnnotationPropertyIriProvider implements Provider<IRI> {

    private final EntityCreationMetadataPreferencesManager preferencesManager;

    @Inject
    public DefaultUserNameAnnotationPropertyIriProvider(EntityCreationMetadataPreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    @Override
    public IRI get() {
        return preferencesManager.getCreatedByAnnotationPropertyIRI();
    }
}
