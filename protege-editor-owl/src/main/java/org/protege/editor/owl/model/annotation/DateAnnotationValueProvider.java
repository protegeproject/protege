package org.protege.editor.owl.model.annotation;

import org.protege.editor.owl.model.util.DateFormatter;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;

import java.util.Date;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29/01/16
 */
public class DateAnnotationValueProvider implements AnnotationValueProvider {

    private final DateFormatter dateFormatter;

    public DateAnnotationValueProvider(DateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    @Override
    public Optional<OWLAnnotationValue> getAnnotationValue(OWLDataFactory dataFactory) {
        return Optional.of(dateFormatter.formatDate(new Date(), dataFactory));
    }
}
