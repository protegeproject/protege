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
public class PubMedLinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp()  {
        extractor = PubMedLinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtractId() {
        String id = "PMID:123456";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(PubMedLinkExtractor.PUBMED_URL_BASE + "123456")));
    }

    @Test
    public void shouldIgnoreCase_AllLowerCase() {
        String id = "pmid:123456";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(PubMedLinkExtractor.PUBMED_URL_BASE + "123456")));
    }

    @Test
    public void shouldIgnoreCase_MixedCase() {
        String id = "PMId:123456";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(PubMedLinkExtractor.PUBMED_URL_BASE + "123456")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfMissingPrefix() {
        String id = "123456";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }

    @Test
    public void shouldAllowWhiteSpaceAfterPrefix() {
        String id = "PMID: 123456";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(PubMedLinkExtractor.PUBMED_URL_BASE + "123456")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfWhiteSpace() {
        String id = "PMID:123 456";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }
}
