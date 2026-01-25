package org.protege.editor.owl.model.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class LiteralLexicalValueReplacer_TestCase {

    private static final String THE_LANG = "TheLang";

    private static final String THE_REPLACEMENT_VALUE = "The Replacement Value";

    private static final String THE_LEXICAL_VALUE = "The Lexical Value";

    private LiteralLexicalValueReplacer replacer;

    @Mock
    private OWLDataFactory dataFactory;

    @Mock
    private OWLLiteral literal;

    @Mock
    private OWLDatatype datatype;

    @Before
    public void setUp() {
        when(literal.getLiteral()).thenReturn(THE_LEXICAL_VALUE);
        when(literal.getDatatype()).thenReturn(datatype);
        when(literal.getLang()).thenReturn(THE_LANG);
        replacer = new LiteralLexicalValueReplacer(dataFactory);
        when(dataFactory.getOWLLiteral(anyString(), any(OWLDatatype.class))).thenReturn(mock(OWLLiteral.class));
        when(dataFactory.getOWLLiteral(anyString(), anyString())).thenReturn(mock(OWLLiteral.class));
    }

    @Test
    public void shouldReplaceLiteralLexicalValueInLiteralWithoutLang() {
        when(literal.hasLang()).thenReturn(false);
        replacer.replaceLexicalValue(literal, THE_REPLACEMENT_VALUE);
        verify(dataFactory, times(1)).getOWLLiteral(THE_REPLACEMENT_VALUE, datatype);
    }

    @Test
    public void shouldReplaceLiteralLexicalValueInLiteralWithLang() {
        when(literal.hasLang()).thenReturn(true);
        replacer.replaceLexicalValue(literal, THE_REPLACEMENT_VALUE);
        verify(dataFactory, times(1)).getOWLLiteral(THE_REPLACEMENT_VALUE, THE_LANG);
    }

    @Test
    public void shouldReplaceLiteralLexicalValueUsingPattern() {
        when(literal.hasLang()).thenReturn(false);
        replacer.replaceLexicalValue(literal, "PREFIX $0");
        verify(dataFactory, times(1)).getOWLLiteral("PREFIX " + THE_LEXICAL_VALUE, datatype);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void shouldThrowIndexOutOfBoundsExceptionOnInvalidPattern() {
        replacer.replaceLexicalValue(literal, "$1");
    }
}
