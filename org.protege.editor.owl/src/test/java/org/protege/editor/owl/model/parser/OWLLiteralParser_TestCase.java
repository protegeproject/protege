package org.protege.editor.owl.model.parser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
@RunWith(MockitoJUnitRunner.class)
public class OWLLiteralParser_TestCase {

    private OWLDataFactory dataFactory;
    private OWLLiteralParser parser;

    @Before
    public void setUp() {
        dataFactory = new OWLDataFactoryImpl();
        parser = new OWLLiteralParser(dataFactory);
    }


    private void assertThatLiteralHasDatatype(OWLLiteral literal, OWL2Datatype datatype) {
        assertThat(literal.getDatatype().getBuiltInDatatype(), is(equalTo(datatype)));
    }

    @Test
    public void shouldParseAsInteger() {
        OWLLiteral literal = parser.parseLiteral("3");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_INTEGER);
    }

    @Test
    public void shouldTrimAndParseAsInteger() {
        OWLLiteral literal = parser.parseLiteral(" 3 ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_INTEGER);
    }

    @Test
    public void shouldParseAsDecimal() {
        OWLLiteral literal = parser.parseLiteral("33.3");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_DECIMAL);
    }

    @Test
    public void shouldTrimAndParseAsDecimal() {
        OWLLiteral literal = parser.parseLiteral(" 33.3 ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_DECIMAL);
    }

    @Test
    public void shouldParseAsFloat() {
        OWLLiteral literal = parser.parseLiteral("33.3f");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_FLOAT);
    }

    @Test
    public void shouldTrimParseAsFloat() {
        OWLLiteral literal = parser.parseLiteral(" 33.3f ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_FLOAT);
    }

    @Test
    public void shouldParseAsFalse() {
        OWLLiteral literal = parser.parseLiteral("false");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
    }

    @Test
    public void shouldParseCapitalisedAsFalse() {
        OWLLiteral literal = parser.parseLiteral("False");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
    }

    @Test
    public void shouldTrimAndParseAsFalse() {
        OWLLiteral literal = parser.parseLiteral(" false ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
    }

    @Test
    public void shouldParseAsTrue() {
        OWLLiteral literal = parser.parseLiteral("true");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
    }

    @Test
    public void shouldParseCapitalisedAsTrue() {
        OWLLiteral literal = parser.parseLiteral("True");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
    }

    @Test
    public void shouldTrimAndParseAsTrue() {
        OWLLiteral literal = parser.parseLiteral(" true ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
    }

    @Test
    public void shouldParseAsRDFPlainLiteral() {
        OWLLiteral literal = parser.parseLiteral("Blah");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.RDF_PLAIN_LITERAL);
    }
}
