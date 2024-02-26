package org.protege.editor.owl.model.util;

import java.util.Date;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 27/01/16
 */
public class TimestampFormatter implements DateFormatter {

    @Override
    public OWLLiteral formatDate(Date date, OWLDataFactory dataFactory) {
        return dataFactory.getOWLLiteral(Long.toString(date.getTime()), OWL2Datatype.XSD_LONG);
    }
}
