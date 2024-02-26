package org.protege.editor.owl.ui.renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the ORCID link extraction.
 * 25 Mar 2022
 */
public class ORCIDLinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp()  {
        extractor = ORCIDLinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtractId() {
        String id = "ORCID:0000-0001-7258-9596";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(ORCIDLinkExtractor.ORCID_URL_BASE + "0000-0001-7258-9596")));
    }

    @Test
    public void shouldIgnoreCase_AllLowerCase() {
        String id = "orcid:0000-0001-7258-9596";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(ORCIDLinkExtractor.ORCID_URL_BASE + "0000-0001-7258-9596")));
    }

    @Test
    public void shouldIgnoreCase_MixedCase() {
        String id = "Orcid:0000-0001-7258-9596";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(ORCIDLinkExtractor.ORCID_URL_BASE + "0000-0001-7258-9596")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfMissingPrefix() {
        String id = "0000-0001-7258-9596";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }

    @Test
    public void shouldAllowWhiteSpaceAfterPrefix() {
        String id = "orcid: 0000-0001-7258-9596";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(ORCIDLinkExtractor.ORCID_URL_BASE + "0000-0001-7258-9596")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfWhiteSpace() {
        String id = "orcid:0000 0001 7258 9596";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }
}
