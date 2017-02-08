package org.protege.editor.owl.model;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.formats.RioTurtleDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Feb 2017
 */
public class DocumentFormatMapper_TestCase {

    private DocumentFormatMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new DocumentFormatMapper();
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        mapper.mapFormat(null);
    }

    @Test
    public void shouldMapRioTurtleFormat() {
        OWLDocumentFormat format = new RioTurtleDocumentFormat();
        OWLDocumentFormat mappedFormat = mapper.mapFormat(format);
        assertThat(mappedFormat, instanceOf(TurtleDocumentFormat.class));
    }

    @Test
    public void shouldMapRioTurtleFormatPrefixes() {
        RioTurtleDocumentFormat format = new RioTurtleDocumentFormat();
        String prefixName = "a:";
        String thePrefix = "ThePrefix";
        format.setPrefix(prefixName, thePrefix);
        OWLDocumentFormat mappedFormat = mapper.mapFormat(format);
        assertThat(mappedFormat.asPrefixOWLOntologyFormat().getPrefix(prefixName), is(thePrefix));
    }

    @Test
    public void shouldPassThroughNonRioTurtleFormat() {
        OWLDocumentFormat format = mock(OWLDocumentFormat.class);
        OWLDocumentFormat mappedFormat = mapper.mapFormat(format);
        assertTrue(format == mappedFormat);
    }
}
