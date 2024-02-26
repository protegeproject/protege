package org.protege.editor.core.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.protege.editor.core.util.StringAbbreviator.ELLIPSIS;

import org.junit.Test;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2 Jun 16
 */
public class StringAbbreviator_TestCase {

    @Test
    public void shouldHandleNullString() {
        String result = StringAbbreviator.abbreviateString(null, 10);
        assertThat(result, is((String)null));
    }

    @Test
    public void shouldHandleEmptyString() {
        String result = StringAbbreviator.abbreviateString("", 10);
        assertThat(result, is(""));
    }

    @Test
    public void shouldHandleZeroLengthForZeroLengthString() {
        String result = StringAbbreviator.abbreviateString("", 0);
        assertThat(result, is(""));
    }

    @Test
    public void shouldHandlePositiveLengthForZeroLengthString() {
        String result = StringAbbreviator.abbreviateString("", 10);
        assertThat(result, is(""));
    }

    @Test
    public void shouldHandleZeroLengthForNonZeroLengthString() {
        String result = StringAbbreviator.abbreviateString("abc", 0);
        assertThat(result, is(ELLIPSIS));
    }

    @Test
    public void shouldHandleNegativeLength() {
        String result = StringAbbreviator.abbreviateString("abc", -1);
        assertThat(result, is(ELLIPSIS));
    }

    @Test
    public void shouldNotAbbreviateString() {
        String result = StringAbbreviator.abbreviateString("abc", 10);
        assertThat(result, is("abc"));
    }

    @Test
    public void shouldNotTrimWhiteSpace() {
        String result = StringAbbreviator.abbreviateString("abc ", 10);
        assertThat(result, is("abc "));
    }

    @Test
    public void shouldNotAbbreviateStringOfEqualLength() {
        String result = StringAbbreviator.abbreviateString("abc", 3);
        assertThat(result, is("abc"));
    }

    @Test
    public void shouldAbbreviateStringOfGreaterLength() {
        String result = StringAbbreviator.abbreviateString("abcde", 3);
        assertThat(result, is("abc" + ELLIPSIS));
    }



}
