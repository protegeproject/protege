package org.protege.editor.owl.model.annotation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public interface AnnotationValueProvider {

    Optional<OWLAnnotationValue> getAnnotationValue(OWLDataFactory dataFactory);
}
