package org.protege.editor.owl.model.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/01/16
 */
@RunWith(MockitoJUnitRunner.class)
public class ISO8601Formatter_TestCase {


    private ISO8601Formatter formatter;


    @Mock
    private OWLDataFactory dataFactory;

    private final Date date = new Date(0);

    @Before
    public void setUp() throws Exception {
        formatter = new ISO8601Formatter();
    }

    @Test
    public void shouldFormatDate() {
        final String expectedValue = "1970-01-01T00:00:00Z";
        formatter.formatDate(date, dataFactory);
        verify(dataFactory, times(1)).getOWLLiteral(expectedValue, OWL2Datatype.XSD_DATE_TIME);
    }
}
