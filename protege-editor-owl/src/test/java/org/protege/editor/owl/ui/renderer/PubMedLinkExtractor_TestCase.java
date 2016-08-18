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

    private PubMedLinkExtractor extractor;

    @Before
    public void setUp()  {
        extractor = new PubMedLinkExtractor();
    }

    @Test
    public void shouldExtractId() {
        String id = "PMID:123456";
        Optional<Integer> extractedId = extractor.extractPubMedId(id);
        assertThat(extractedId, is(Optional.of(123456)));
    }

    @Test
    public void shouldNotExtractIdInCaseOfMissingPrefix() {
        String id = "123456";
        Optional<Integer> extractedId = extractor.extractPubMedId(id);
        assertThat(extractedId.isPresent(), is(false));
    }

    @Test
    public void shouldNotExtractIdInCaseOfWhiteSpace() {
        String id = "PMID:123 456";
        Optional<Integer> extractedId = extractor.extractPubMedId(id);
        assertThat(extractedId.isPresent(), is(false));
    }
}
