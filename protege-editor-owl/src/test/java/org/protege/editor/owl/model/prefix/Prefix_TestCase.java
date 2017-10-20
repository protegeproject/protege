package org.protege.editor.owl.model.prefix;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.protege.editor.owl.model.prefix.Prefix.PREFIX_NAME;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 19 Oct 2017
 */
public class Prefix_TestCase {

    @Test
    public void shouldMatchPrefixName() {
        assertThat(PREFIX_NAME.matcher("owl:").matches(), is(true));
    }

    @Test
    public void shouldMatchEmptyPrefixName() {
        assertThat(PREFIX_NAME.matcher(":").matches(), is(true));
    }

    @Test
    public void shouldNotMatchPrefixNameMissingColon() {
        assertThat(PREFIX_NAME.matcher("owl").matches(), is(false));
    }

    @Test
    public void shouldMatchPrefixNameWithDashes() {
        assertThat(PREFIX_NAME.matcher("a-b:").matches(), is(true));
    }

    @Test
    public void shouldMatchPrefixNameWithMultipleColons() {
        assertThat(PREFIX_NAME.matcher("owl:etc:").matches(), is(true));
    }

    @Test
    public void shouldNotMatchPrefixNameStartingWithNumber() {
        assertThat(PREFIX_NAME.matcher("7etc:").matches(), is(true));
    }

}
