package org.protege.editor.owl.ui.renderer;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class ISBN10LinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp() {
        extractor = ISBN10LinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtractISBN10() {
        Optional<String> extractedIsbn = extractor.extractLinkLiteral("ISBN:0123456789");
        assertThat(extractedIsbn, is(Optional.of(ISBN10LinkExtractor.URL_BASE + "0123456789")));
    }

    @Test
    public void shouldNotExtractInvalidISBN10_TooShort() {
        Optional<String> extractedIsbn = extractor.extractLinkLiteral("ISBN:012345678");
        assertThat(extractedIsbn.isPresent(), is(false));
    }

    @Test
    public void shouldNotExtractInvalidISBN10_TooLong() {
        Optional<String> extractedIsbn = extractor.extractLinkLiteral("ISBN:01234567890");
        assertThat(extractedIsbn.isPresent(), is(false));
    }

    @Test
    public void shouldNotExtractInvalidISBN10_AlphaNumeric() {
        Optional<String> extractedIsbn = extractor.extractLinkLiteral("ISBN:012345678A");
        assertThat(extractedIsbn.isPresent(), is(false));
    }

    @Test
    public void shouldNotExtractInvalidISBN10_WhiteSpace() {
        Optional<String> extractedIsbn = extractor.extractLinkLiteral("ISBN:0123 45678");
        assertThat(extractedIsbn.isPresent(), is(false));
    }


}
