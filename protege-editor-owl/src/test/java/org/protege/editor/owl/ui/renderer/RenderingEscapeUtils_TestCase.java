package org.protege.editor.owl.ui.renderer;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Feb 2018
 */
public class RenderingEscapeUtils_TestCase {

    @Test
    public void shouldNotEscapeRendering() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("AB");
        assertThat(rendering, is("AB"));
    }

    @Test
    public void shouldEscapeRenderingContainingSpace() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A B");
        assertThat(rendering, is("'A B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingComma() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A,B");
        assertThat(rendering, is("'A,B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingLeftBracket() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A(B");
        assertThat(rendering, is("'A(B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingRightBracket() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A)B");
        assertThat(rendering, is("'A)B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingLeftSquareBracket() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A[B");
        assertThat(rendering, is("'A[B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingRightSquareBracket() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A]B");
        assertThat(rendering, is("'A]B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingLeftBrace() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A{B");
        assertThat(rendering, is("'A{B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingRightBrace() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A}B");
        assertThat(rendering, is("'A}B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingHat() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A^B");
        assertThat(rendering, is("'A^B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingAt() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A@B");
        assertThat(rendering, is("'A@B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingLessThan() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A<B");
        assertThat(rendering, is("'A<B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingGreaterThan() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A>B");
        assertThat(rendering, is("'A>B'"));
    }

    @Test
    public void shouldEscapeRenderingContainingEquals() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A=B");
        assertThat(rendering, is("'A=B'"));
    }

    @Test
    public void shouldEscapeSingleQuote() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A's");
        assertThat(rendering, is("A\\'s"));
    }

    @Test
    public void shouldEscapeSingleQuoteWithSpaces() {
        String rendering = RenderingEscapeUtils.getEscapedRendering("A's and B's");
        assertThat(rendering, is("'A\\'s and B\\'s'"));
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
