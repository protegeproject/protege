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
public class OMIMPSLinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp()  {
        extractor = OMIMPSLinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtractId() {
        String id = "OMIMPS:236100";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OMIMPSLinkExtractor.OMIMPS_URL_BASE + "236100")));
    }

    @Test
    public void shouldIgnoreCase_AllLowerCase() {
        String id = "omimps:236100";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OMIMPSLinkExtractor.OMIMPS_URL_BASE + "236100")));
    }

    @Test
    public void shouldIgnoreCase_MixedCase() {
        String id = "OmimPS:236100";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OMIMPSLinkExtractor.OMIMPS_URL_BASE + "236100")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfMissingPrefix() {
        String id = "236100";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }

    @Test
    public void shouldAllowWhiteSpaceAfterPrefix() {
        String id = "OMIMPS: 236100";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId, is(Optional.of(OMIMPSLinkExtractor.OMIMPS_URL_BASE + "236100")));
    }

    @Test
    public void shouldNotExtractIdInCaseOfWhiteSpace() {
        String id = "OMIMPS:236 100";
        Optional<String> extractedId = extractor.extractLinkLiteral(id);
        assertThat(extractedId.isPresent(), is(false));
    }
}
