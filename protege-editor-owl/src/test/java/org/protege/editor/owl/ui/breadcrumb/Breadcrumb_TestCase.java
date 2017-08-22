
package org.protege.editor.owl.ui.breadcrumb;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLObject;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class Breadcrumb_TestCase {

    private Breadcrumb breadcrumb;
    @Mock
    private OWLObject object;
    @Mock
    private Object parentRelationship;

    @Before
    public void setUp() {
        breadcrumb = new Breadcrumb(object, parentRelationship);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_object_IsNull() {
        new Breadcrumb(null, parentRelationship);
    }

    @Test
    public void shouldReturnSupplied_object() {
        assertThat(breadcrumb.getObject(), is(this.object));
    }

    @Test
    public void shouldReturnSupplied_parentRelationship() {
        assertThat(breadcrumb.getParentRelationship(), is(Optional.of(this.parentRelationship)));
    }

    @Test
    public void shouldBeEqualToSelf() {
        assertThat(breadcrumb, is(breadcrumb));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void shouldNotBeEqualToNull() {
        assertThat(breadcrumb.equals(null), is(false));
    }

    @Test
    public void shouldBeEqualToOther() {
        assertThat(breadcrumb, is(new Breadcrumb(object, parentRelationship)));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_object() {
        assertThat(breadcrumb, is(not(new Breadcrumb(mock(OWLObject.class), parentRelationship))));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_parentRelationship() {
        assertThat(breadcrumb, is(not(new Breadcrumb(object, mock(Object.class)))));
    }

    @Test
    public void shouldBeEqualToOtherHashCode() {
        assertThat(breadcrumb.hashCode(), is(new Breadcrumb(object, parentRelationship).hashCode()));
    }

    @Test
    public void shouldImplementToString() {
        assertThat(breadcrumb.toString(), Matchers.startsWith("Breadcrumb"));
    }

}
