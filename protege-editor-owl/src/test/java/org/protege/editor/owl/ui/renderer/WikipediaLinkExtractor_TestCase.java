package org.protege.editor.owl.ui.renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class WikipediaLinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp() {
        extractor = WikipediaLinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtrackWikipediaLink() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("Wikipedia:Boeing_747");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/Boeing_747")));
    }

    @Test
    public void shouldIgnoreCase_LowerCase() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("wikipedia:Boeing_747");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/Boeing_747")));
    }

    @Test
    public void shouldIgnoreCase_UpperCase() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WIKIPEDIA:Boeing_747");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/Boeing_747")));
    }

    @Test
    public void shouldExtractLinkWithBrackets() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WIKIPEDIA:Boeing_(747)");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/Boeing_(747)")));
    }

    @Test
    public void shouldExtractLinkWithHyphens() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WIKIPEDIA:Boeing-747");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/Boeing-747")));
    }

    @Test
    public void shouldNotExtractLinkWithSpaces() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("wikipedia:Boeing 747");
        assertThat(extractedLink.isPresent(), is(false));
    }

    @Test
    public void shouldNotExtractLinkWithTabs() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("wikipedia:Boeing\t747");
        assertThat(extractedLink.isPresent(), is(false));
    }
}
