
package org.protege.editor.owl.model.hierarchy.tabbed;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@RunWith(MockitoJUnitRunner.class)
public class Edge_TestCase {

    private Edge edge;

    private String child = "The child";

    private String parent = "The parent";

    @Before
    public void setUp() {
        edge = new Edge(child, parent);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIf_child_IsNull() {
        new Edge(null, parent);
    }

    @Test
    public void shouldReturnSupplied_child() {
        assertThat(edge.getChild(), is(this.child));
    }

    @Test
    public void shouldReturnSupplied_parent() {
        assertThat(edge.getParent(), is(this.parent));
    }

    @Test
    public void shouldBeEqualToSelf() {
        assertThat(edge, is(edge));
    }

    @Test
    public void shouldNotBeEqualToNull() {
        assertThat(edge.equals(null), is(false));
    }

    @Test
    public void shouldBeEqualToOther() {
        assertThat(edge, is(new Edge(child, parent)));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_child() {
        assertThat(edge, is(not(new Edge("String-733cb0c1-0862-4265-b29a-e87a3d889415", parent))));
    }

    @Test
    public void shouldNotBeEqualToOtherThatHasDifferent_parent() {
        assertThat(edge, is(not(new Edge(child, "String-d22d31b1-ea05-4967-bdbd-4b15afa02cf2"))));
    }

    @Test
    public void shouldBeEqualToOtherHashCode() {
        assertThat(edge.hashCode(), is(new Edge(child, parent).hashCode()));
    }

    @Test
    public void shouldImplementToString() {
        assertThat(edge.toString(), startsWith("Edge"));
    }

    @Test
    public void should_getChildName() {
        assertThat(edge.getChildName(), is(child));
    }

    @Test
    public void should_getParentName() {
        assertThat(edge.getParentName(), is(Optional.of(parent)));
    }

    @Test
    public void shouldReturn_true_For_isRoot() {
        Edge rootEdge = new Edge("Child", null);
        assertThat(rootEdge.isRoot(), is(true));
    }

    @Test
    public void shouldReturn_false_For_isRoot() {
        assertThat(edge.isRoot(), is(false));
    }

}
