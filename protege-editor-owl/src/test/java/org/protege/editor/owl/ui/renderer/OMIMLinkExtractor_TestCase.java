package org.protege.editor.owl.ui.renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the OMIM link extraction.
 * 25 Mar 2022
 */
public class OMIMLinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp()  {
        extractor = OMIMLinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtractId() {
        String id = "OMIM:300376";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OMIMLinkExtractor.OMIM_URL_BASE + "300376")));
    }

    @Test
    public void shouldIgnoreCase_AllLowerCase() {
        String id = "omim:300376";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OMIMLinkExtractor.OMIM_URL_BASE + "300376")));
    }

    @Test
    public void shouldIgnoreCase_MixedCase() {
        String id = "OMim:300376";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OMIMLinkExtractor.OMIM_URL_BASE + "300376")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfMissingPrefix() {
        String id = "300376";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }

    @Test
    public void shouldAllowWhiteSpaceAfterPrefix() {
        String id = "OMIM: 300376";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OMIMLinkExtractor.OMIM_URL_BASE + "300376")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfWhiteSpace() {
        String id = "OMIM:300 376";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }
}
