
package org.protege.osgi.framework;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class SymbolicName_TestCase {

    private SymbolicName symbolicName;

    private String name = "The name";

    @Before
    public void setUp()
            throws Exception {
        symbolicName = new SymbolicName(name);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_name_IsNull() {
        new SymbolicName(null);
    }

    @Test
    public void shouldReturnSupplied_name() {
        assertThat(symbolicName.getName(), is(this.name));
    }

    @Test
    public void shouldBeEqualToSelf() {
        assertThat(symbolicName, is(symbolicName));
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertThat(symbolicName.equals(null), is(false));
    }

    @Test
    public void shouldBeEqualToOther() {
        assertThat(symbolicName, is(new SymbolicName(name)));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_name() {
        assertThat(symbolicName, is(Matchers.not(new SymbolicName("String-46c34f26-920b-41d9-9401-cd66d80caf4a"))));
    }

    @Test
    public void shouldBeEqualToOtherHashCode() {
        assertThat(symbolicName.hashCode(), is(new SymbolicName(name).hashCode()));
    }

    @Test
    public void shouldImplementToString() {
        assertThat(symbolicName.toString(), Matchers.startsWith("SymbolicName"));
    }

}
