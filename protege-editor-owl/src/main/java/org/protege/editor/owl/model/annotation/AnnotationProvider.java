package org.protege.editor.owl.model.annotation;

import java.util.Optional;

import javax.inject.Provider;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public final class AnnotationProvider {

    private final Provider<IRI> annotationPropertyIriProvider;

    private final AnnotationValueProvider annotationValueProvider;

    public AnnotationProvider(Provider<IRI> annotationPropertyIriProvider,
                              AnnotationValueProvider annotationValueProvider) {
        this.annotationPropertyIriProvider = annotationPropertyIriProvider;
        this.annotationValueProvider = annotationValueProvider;
    }

    public Optional<OWLAnnotation> getAnnotation(OWLDataFactory dataFactory) {
        Optional<OWLAnnotationValue> annotationValue = annotationValueProvider.getAnnotationValue(dataFactory);
        return annotationValue.map(v -> {
                    IRI iri = annotationPropertyIriProvider.get();
                    OWLAnnotationProperty property = dataFactory.getOWLAnnotationProperty(iri);
                    return dataFactory.getOWLAnnotation(property, annotationValue.get());
                }
        );
    }

    public Optional<OWLAnnotation> getAnnotation(OWLEditorKit editorKit) {
        return Optional.empty();
    }
}
