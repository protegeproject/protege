
package org.protege.editor.owl.ui.renderer;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.protege.editor.owl.ui.renderer.layout.HTTPLink;
import org.protege.editor.owl.ui.renderer.layout.Link;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@RunWith(MockitoJUnitRunner.class)
public class RegExBasedLinkExtractor_TestCase {

    private RegExBasedLinkExtractor regExBasedLinkExtractor;

    private String name = "The name";

    private Pattern linkPattern = Pattern.compile("LinkPrefix:(.+)");

    private String replacementString = "TheActualLinkAddress-$1.test";

    @Before
    public void setUp() {
        regExBasedLinkExtractor = new RegExBasedLinkExtractor(name, linkPattern, replacementString);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_name_IsNull() {
        new RegExBasedLinkExtractor(null, linkPattern, replacementString);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_linkPattern_IsNull() {
        new RegExBasedLinkExtractor(name, null, replacementString);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_replacementString_IsNull() {
        new RegExBasedLinkExtractor(name, linkPattern, null);
    }

    @Test
    public void shouldBeEqualToSelf() {
        assertThat(regExBasedLinkExtractor, is(regExBasedLinkExtractor));
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertThat(regExBasedLinkExtractor.equals(null), is(false));
    }

    @Test
    public void shouldBeEqualToOther() {
        assertThat(regExBasedLinkExtractor, is(new RegExBasedLinkExtractor(name, linkPattern, replacementString)));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_name() {
        assertThat(regExBasedLinkExtractor, is(not(new RegExBasedLinkExtractor("String-2862fbdb-7c03-47a9-a852-3e4664bf5a01", linkPattern, replacementString))));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_linkPattern() {
        assertThat(regExBasedLinkExtractor, is(not(new RegExBasedLinkExtractor(name, Pattern.compile("OtherPattern"), replacementString))));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_replacementString() {
        assertThat(regExBasedLinkExtractor, is(not(new RegExBasedLinkExtractor(name, linkPattern, "String-9259a63d-3305-4df2-b8e3-401711868abd"))));
    }

    @Test
    public void shouldBeEqualToOtherHashCode() {
        assertThat(regExBasedLinkExtractor.hashCode(), is(new RegExBasedLinkExtractor(name, linkPattern, replacementString).hashCode()));
    }

    @Test
    public void shouldImplementToString() {
        assertThat(regExBasedLinkExtractor.toString(), startsWith("RegExBasedLinkExtractor"));
    }

    @Test
    public void should_extractLink() {
        Optional<Link> theLink = regExBasedLinkExtractor.extractLink("LinkPrefix:1234");
        assertThat(theLink, is(Optional.of(new HTTPLink(URI.create("TheActualLinkAddress-1234.test")))));
    }

    @Test
    public void should_not_extractLink() {
        assertThat(regExBasedLinkExtractor.extractLink("WrongLinkPrefix:1234"), is(Optional.<Link>empty()));
    }

}
