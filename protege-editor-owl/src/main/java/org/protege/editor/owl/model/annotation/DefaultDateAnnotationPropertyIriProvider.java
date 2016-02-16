package org.protege.editor.owl.model.annotation;

import org.semanticweb.owlapi.model.IRI;

import javax.inject.Provider;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 27/01/16
 */
public class DefaultDateAnnotationPropertyIriProvider implements Provider<IRI> {

    private final EntityCreationMetadataPreferencesManager preferencesManager;

    public DefaultDateAnnotationPropertyIriProvider(EntityCreationMetadataPreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    @Override
    public IRI get() {
        return preferencesManager.getCreationDateAnnotationPropertyIRI();
    }
}
