package org.protege.editor.owl.ui.renderer;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Unit tests for the DOI link extraction.
 * 25 Mar 2022
 */
public class DOILinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp()  {
        extractor = DOILinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtractId() {
        String id = "DOI:10.1101/2021.10.10.463703";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(DOILinkExtractor.DOI_URL_BASE + "10.1101/2021.10.10.463703")));
    }

    @Test
    public void shouldIgnoreCase_AllLowerCase() {
        String id = "doi:10.1101/2021.10.10.463703";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(DOILinkExtractor.DOI_URL_BASE + "10.1101/2021.10.10.463703")));
    }

    @Test
    public void shouldIgnoreCase_MixedCase() {
        String id = "DoI:10.1101/2021.10.10.463703";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(DOILinkExtractor.DOI_URL_BASE + "10.1101/2021.10.10.463703")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfMissingPrefix() {
        String id = "10.1101/2021.10.10.463703";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }

    @Test
    public void shouldAllowWhiteSpaceAfterPrefix() {
        String id = "doi: 10.1101/2021.10.10.463703";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(DOILinkExtractor.DOI_URL_BASE + "10.1101/2021.10.10.463703")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfWhiteSpace() {
        String id = "doi:123 456";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }
}
