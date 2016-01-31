package org.protege.editor.owl.model.annotation;

import org.semanticweb.owlapi.model.*;

import javax.inject.Provider;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public class AnnotationProvider {

    private final Provider<IRI> annotationPropertyIriProvider;

    private final AnnotationValueProvider annotationValueProvider;

    public AnnotationProvider(Provider<IRI> annotationPropertyIriProvider, AnnotationValueProvider annotationValueProvider) {
        this.annotationPropertyIriProvider = annotationPropertyIriProvider;
        this.annotationValueProvider = annotationValueProvider;
    }

    Optional<OWLAnnotation> getAnnotation(OWLDataFactory dataFactory) {
        Optional<OWLAnnotationValue> annotationValue = annotationValueProvider.getAnnotationValue(dataFactory);
        if(!annotationValue.isPresent()) {
            return Optional.empty();
        }
        OWLAnnotationProperty property = dataFactory.getOWLAnnotationProperty(annotationPropertyIriProvider.get());
        return Optional.of(dataFactory.getOWLAnnotation(property, annotationValue.get()));
    }
}
