package org.protege.editor.owl.model.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

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

    private void assertThatLiteralValueIs(OWLLiteral literal, String value) {
        assertThat(literal.getLiteral(), is(value));
    }

    @Test
    public void shouldParseZeroAsDecimal() {
        OWLLiteral literal = parser.parseLiteral("0");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_DECIMAL);
        assertThatLiteralValueIs(literal, "0");
    }

    @Test
    public void shouldParseOneAsDecimalDecimal() {
        OWLLiteral literal = parser.parseLiteral("1");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_DECIMAL);
        assertThatLiteralValueIs(literal, "1");
    }


    @Test
    public void shouldParseAsIntegerDecimal() {
        OWLLiteral literal = parser.parseLiteral("3");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_DECIMAL);
        assertThatLiteralValueIs(literal, "3");
    }

    @Test
    public void shouldTrimAndParseWholeNumberAsDecimal() {
        OWLLiteral literal = parser.parseLiteral(" 3 ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_DECIMAL);
        assertThatLiteralValueIs(literal, "3");
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
        assertThatLiteralValueIs(literal, "33.3");
    }

    @Test
    public void shouldParseAsFloat() {
        OWLLiteral literal = parser.parseLiteral("33.3f");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_FLOAT);
        assertThatLiteralValueIs(literal, "33.3");
    }

    @Test
    public void shouldTrimParseAsFloat() {
        OWLLiteral literal = parser.parseLiteral(" 33.3f ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_FLOAT);
        assertThatLiteralValueIs(literal, "33.3");
    }

    @Test
    public void shouldParseAsFalse() {
        OWLLiteral literal = parser.parseLiteral("false");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
        assertThatLiteralValueIs(literal, "false");
    }

    @Test
    public void shouldParseCapitalisedAsFalse() {
        OWLLiteral literal = parser.parseLiteral("False");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
        assertThatLiteralValueIs(literal, "false");
    }

    @Test
    public void shouldTrimAndParseAsFalse() {
        OWLLiteral literal = parser.parseLiteral(" false ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
        assertThatLiteralValueIs(literal, "false");
    }

    @Test
    public void shouldParseAsTrue() {
        OWLLiteral literal = parser.parseLiteral("true");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
        assertThatLiteralValueIs(literal, "true");
    }

    @Test
    public void shouldParseCapitalisedAsTrue() {
        OWLLiteral literal = parser.parseLiteral("True");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
        assertThatLiteralValueIs(literal, "true");
    }

    @Test
    public void shouldTrimAndParseAsTrue() {
        OWLLiteral literal = parser.parseLiteral(" true ");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_BOOLEAN);
        assertThatLiteralValueIs(literal, "true");
    }

    @Test
    public void shouldParseAsString() {
        OWLLiteral literal = parser.parseLiteral("Blah");
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_STRING);
        assertThatLiteralValueIs(literal, "Blah");
    }

    @Test
    public void shouldPreserveCase() {
        OWLLiteral literal = parser.parseLiteral("Blah");
        assertThat(literal.getLiteral(), is("Blah"));
    }

    @Test
    public void shouldNotParseOutLang() {
        OWLLiteral literal = parser.parseLiteral("Hello@World");
        assertThat(literal.getLiteral(), is("Hello@World"));
        assertThatLiteralHasDatatype(literal, OWL2Datatype.XSD_STRING);
    }
}
