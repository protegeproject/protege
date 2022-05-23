
package org.protege.editor.owl.model.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class Orcid_TestCase {

    private Orcid orcid;

    private String value = "0000-1234-5678-0000";

    @Before
    public void setUp()
        throws Exception
    {
        orcid = new Orcid(value);
    }

    @Test(expected = java.lang.NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_value_IsNull() {
        new Orcid(null);
    }

    @Test
    public void shouldReturnSupplied_value() {
        assertThat(orcid.getValue(), is(this.value));
    }

    @Test
    public void shouldBeEqualToSelf() {
        assertThat(orcid, is(orcid));
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertThat(orcid.equals(null), is(false));
    }

    @Test
    public void shouldBeEqualToOther() {
        assertThat(orcid, is(new Orcid(value)));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_value() {
        assertThat(orcid, is(not(new Orcid("0000-1234-5678-111X"))));
    }

    @Test
    public void shouldBeEqualToOtherHashCode() {
        assertThat(orcid.hashCode(), is(new Orcid(value).hashCode()));
    }

    @Test
    public void shouldImplementToString() {
        assertThat(orcid.toString(), startsWith("Orcid"));
    }

    public void shouldGetUri() {
        assertThat(orcid.toUri(), is(URI.create("https://orcid.org/" + value)));
    }

}
