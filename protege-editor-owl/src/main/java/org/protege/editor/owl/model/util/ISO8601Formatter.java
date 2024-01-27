package org.protege.editor.owl.model.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 *
 * Formats a date in ISO 8601 format with a UTC (Zulu) time zone.
 */
public class ISO8601Formatter implements DateFormatter {

    private static final String ISO_8601_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private final DateFormat df;

    public ISO8601Formatter() {
        df = new SimpleDateFormat(ISO_8601_UTC_FORMAT);
        df.setTimeZone(UTC);
    }

    @Override
    public OWLLiteral formatDate(Date date, OWLDataFactory dataFactory) {
        String format = df.format(date);
        return dataFactory.getOWLLiteral(format, OWL2Datatype.XSD_DATE_TIME);
    }
}
