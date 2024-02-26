package org.protege.editor.owl.model.annotation;

import java.util.Optional;

import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public interface AnnotationValueProvider {

    Optional<OWLAnnotationValue> getAnnotationValue(OWLDataFactory dataFactory);
}
