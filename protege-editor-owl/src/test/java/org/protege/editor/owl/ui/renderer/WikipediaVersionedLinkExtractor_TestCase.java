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
public class WikipediaVersionedLinkExtractor_TestCase {

    private RegExBasedLinkExtractor extractor;

    @Before
    public void setUp() {
        extractor = WikipediaVersionedLinkExtractor.createExtractor();
    }

    @Test
    public void shouldExtrackWikipediaVersionedLink() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WikipediaVersioned:Boeing_747&oldid=1078029055");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/index.php?title=Boeing_747&oldid=1078029055")));
    }

    @Test
    public void shouldIgnoreCase_LowerCase() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("wikipediaversioned:Boeing_747&oldid=1078029055");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/index.php?title=Boeing_747&oldid=1078029055")));
    }

    @Test
    public void shouldIgnoreCase_UpperCase() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WIKIPEDIAVERSIONED:Boeing_747&oldid=1078029055");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/index.php?title=Boeing_747&oldid=1078029055")));
    }

    @Test
    public void shouldExtractLinkWithBrackets() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WIKIPEDIAVERSIONED:Boeing_(747)&oldid=1078029055");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/index.php?title=Boeing_(747)&oldid=1078029055")));
    }

    @Test
    public void shouldExtractLinkWithHyphens() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WIKIPEDIAVERSIONED:Boeing-747&oldid=1078029055");
        assertThat(extractedLink, is(Optional.of("https://wikipedia.org/wiki/index.php?title=Boeing-747&oldid=1078029055")));
    }

    @Test
    public void shouldNotExtractLinkWithSpaces() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WikipediaVersioned:Boeing 747&oldid=1078029055");
        assertThat(extractedLink.isPresent(), is(false));
    }

    @Test
    public void shouldNotExtractLinkWithTabs() {
        Optional<String> extractedLink = extractor.extractLinkLiteral("WikipediaVersioned:Boeing\t747&oldid=1078029055");
        assertThat(extractedLink.isPresent(), is(false));
    }
}
