package org.protege.editor.owl.ui.renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the orphanet link extraction.
 * 25 Mar 2022
 */
public class OrphanetLinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp()  {
        extractor = OrphanetLinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtractId() {
        String id = "Orphanet:276422";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OrphanetLinkExtractor.ORPHANET_URL_BASE + "276422")));
    }

    @Test
    public void shouldIgnoreCase_AllLowerCase() {
        String id = "orphanet:276422";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OrphanetLinkExtractor.ORPHANET_URL_BASE + "276422")));
    }

    @Test
    public void shouldIgnoreCase_MixedCase() {
        String id = "OrphaNet:276422";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OrphanetLinkExtractor.ORPHANET_URL_BASE + "276422")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfMissingPrefix() {
        String id = "276422";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }

    @Test
    public void shouldAllowWhiteSpaceAfterPrefix() {
        String id = "Orphanet: 276422";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OrphanetLinkExtractor.ORPHANET_URL_BASE + "276422")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfWhiteSpace() {
        String id = "Oprhanet:276 422";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }
}
