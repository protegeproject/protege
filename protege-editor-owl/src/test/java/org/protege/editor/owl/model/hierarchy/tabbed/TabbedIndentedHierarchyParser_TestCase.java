package org.protege.editor.owl.model.hierarchy.tabbed;

import com.google.common.base.Objects;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.protege.editor.owl.model.hierarchy.tabbed.TabbedIndentedHierarchyParser_TestCase.EdgeMatcher.edge;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 Sep 16
 */
public class TabbedIndentedHierarchyParser_TestCase {

    public static final int INDENT = 4;

    public static final String PREFIX = "Prefix";

    public static final String SUFFIX = "Suffix";

    public static final String A_PARSED = "PrefixASuffix";

    public static final String B_PARSED = "PrefixBSuffix";

    private TabIndentedHierarchyParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new TabIndentedHierarchyParser(INDENT, PREFIX, SUFFIX);
    }

    @Test
    public void shouldParseSingleLine() throws IOException {
        List<Edge> edges = parser.parse(new StringReader("A"));
        assertThat(edges, hasItem(edge(A_PARSED, null)));
    }

    @Test
    public void shouldParseSubClassOf() throws IOException {
        List<Edge> edges = parser.parse(new StringReader("A\n\tB"));
        assertThat(edges, hasItem(edge(B_PARSED, A_PARSED)));
    }

    @Test
    public void shouldParseSubClassOfWithExtraSpace() throws IOException {
        List<Edge> edges = parser.parse(new StringReader("A\n\t\tB"));
        assertThat(edges, hasItem(edge(B_PARSED, A_PARSED)));
    }

    @Test
    public void shouldIgnoreLeadingSpace() throws IOException {
        List<Edge> edges = parser.parse(new StringReader("\tA\n\t\tB"));
        assertThat(edges, hasItem(edge(B_PARSED, A_PARSED)));
    }

    @Test
    public void shouldParseNameWithSpaces() throws IOException {
        String nameWithSpaces = "A Name With Spaces";
        List<Edge> edges = parser.parse(new StringReader("\t" + nameWithSpaces + "\n\t\tB"));
        assertThat(edges, hasItem(edge(B_PARSED, PREFIX + nameWithSpaces + SUFFIX)));
    }


    protected static class EdgeMatcher extends TypeSafeMatcher<Edge> {

        private String child;

        private String parent;

        public EdgeMatcher(@Nonnull String child, @Nullable String parent) {
            this.child = child;
            this.parent = parent;
        }

        @Override
        protected boolean matchesSafely(Edge item) {
            return Objects.equal(parent, item.getParent()) && item.getChild().equals(child);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(new Edge(child, parent).toString());
        }

        public static EdgeMatcher edge(@Nonnull String child, @Nullable String parent) {
            return new EdgeMatcher(child, parent);
        }
    }
}
