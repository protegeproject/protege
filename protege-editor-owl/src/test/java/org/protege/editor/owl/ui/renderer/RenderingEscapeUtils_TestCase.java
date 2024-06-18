package org.protege.editor.owl.ui.renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Feb 2018
 */
public class RenderingEscapeUtils_TestCase {

	private static void assertEscaped(String original, String escaped) {
		String actualEscaped = RenderingEscapeUtils.getEscapedRendering(original);
		assertThat(actualEscaped, Matchers.is(escaped));
		String actualUnescaped = RenderingEscapeUtils.unescape(escaped);
		assertThat(actualUnescaped, Matchers.is(original));
	}
	
    @Test
    public void shouldNotEscapeRendering() {
    	assertEscaped("AB", "AB");
    }

    @Test
	public void shouldEscapeRenderingContainingSpace() {
		assertEscaped("A B", "'A B'");
	}

    @Test
    public void shouldEscapeRenderingContainingComma() {
    	assertEscaped("A,B", "'A,B'");
    }

    @Test
    public void shouldEscapeRenderingContainingLeftBracket() {
    	assertEscaped("A(B", "'A(B'");
    }

    @Test
    public void shouldEscapeRenderingContainingRightBracket() {
    	assertEscaped("A)B", "'A)B'");
    }

    @Test
    public void shouldEscapeRenderingContainingLeftSquareBracket() {
    	assertEscaped("A[B", "'A[B'");
    }

    @Test
    public void shouldEscapeRenderingContainingRightSquareBracket() {
    	assertEscaped("A]B", "'A]B'");
    }

    @Test
    public void shouldEscapeRenderingContainingLeftBrace() {
    	assertEscaped("A{B", "'A{B'");
    }

    @Test
    public void shouldEscapeRenderingContainingRightBrace() {
    	assertEscaped("A}B", "'A}B'");
    }

    @Test
    public void shouldEscapeRenderingContainingHat() {
    	assertEscaped("A^B", "'A^B'");
    }

    @Test
    public void shouldEscapeRenderingContainingAt() {
    	assertEscaped("A@B", "'A@B'");
    }

    @Test
    public void shouldEscapeRenderingContainingLessThan() {
    	assertEscaped("A<B", "'A<B'");
    }

    @Test
    public void shouldEscapeRenderingContainingGreaterThan() {
    	assertEscaped("A>B", "'A>B'");
    }

    @Test
    public void shouldEscapeRenderingContainingEquals() {
    	assertEscaped("A=B", "'A=B'");
    }

    @Test
    public void shouldEscapeSingleQuote() {
    	assertEscaped("A'B", "A\\'B");
    }
    
    @Test
    public void shouldEscapeDoubleQuote() {
    	assertEscaped("A\"B", "A\\\"B");
    }
    
    @Test
    public void shouldEscapeBackslash() {
    	assertEscaped("A\\B", "'A\\\\B'");
    }

    @Test
    public void shouldEscapeSingleQuoteWithSpaces() {
    	assertEscaped("A's and B's", "'A\\'s and B\\'s'");    	
    }

    @Test
    public void shouldUnescapeRendering() {
        String rendering = RenderingEscapeUtils.unescape("'A'");
        assertThat(rendering, is("A"));
    }

    @Test
    public void shouldUnescapeRenderingWithEscapedQuote() {
        String rendering = RenderingEscapeUtils.unescape("'A\\'s'");
        assertThat(rendering, is("A's"));
    }
}
