package org.protege.editor.owl.model.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 Feb 2018
 */
public class IriSplitter_TestCase {

    private IriSplitter splitter;

    @Before
    public void setUp() {
        splitter = new IriSplitter();
    }

    @Test
    public void shouldNotSplitEmptyString() {
        assertThat(splitter.getShortName(IRI.create("")), is(Optional.empty()));
    }

    @Test
    public void shouldNotSplitHost() {
        assertThat(splitter.getShortName(IRI.create("http://example.com")), is(Optional.empty()));
    }

    @Test
    public void shouldNotSplitEmptyPathIri() {
        assertThat(splitter.getShortName(IRI.create("http://example.com/")), is(Optional.empty()));
    }

    @Test
    public void shouldNotSplitEmptySlashIri() {
        assertThat(splitter.getShortName(IRI.create("http://example.com#")), is(Optional.empty()));
    }

    @Test
    public void shouldSplitUsingSlashOnLetter() {
        assertThat(splitter.getShortName(IRI.create("http://example.com/A")), is(Optional.of("A")));
    }

    @Test
    public void shouldSplitUsingSlashOnLetters() {
        assertThat(splitter.getShortName(IRI.create("http://example.com/Abc")), is(Optional.of("Abc")));
    }

    @Test
    public void shouldSplitUsingSlashOnNumber() {
        assertThat(splitter.getShortName(IRI.create("http://example.com/3")), is(Optional.of("3")));
    }

    @Test
    public void shouldSplitUsingSlashOnNumbers() {
        assertThat(splitter.getShortName(IRI.create("http://example.com/123")), is(Optional.of("123")));
    }

    @Test
    public void shouldSplitUsingSlashOnNumbersAndLetters() {
        assertThat(splitter.getShortName(IRI.create("http://example.com/123ABC")), is(Optional.of("123ABC")));
    }


    @Test
    public void shouldSplitUsingHashOnLetter() {
        assertThat(splitter.getShortName(IRI.create("http://example.com#A")), is(Optional.of("A")));
    }

    @Test
    public void shouldSplitUsingHashOnLetters() {
        assertThat(splitter.getShortName(IRI.create("http://example.com#Abc")), is(Optional.of("Abc")));
    }

    @Test
    public void shouldSplitUsingHashOnNumber() {
        assertThat(splitter.getShortName(IRI.create("http://example.com#3")), is(Optional.of("3")));
    }

    @Test
    public void shouldSplitUsingHashOnNumbers() {
        assertThat(splitter.getShortName(IRI.create("http://example.com#123")), is(Optional.of("123")));
    }

    @Test
    public void shouldSplitUsingHashOnNumbersAndLetters() {
        assertThat(splitter.getShortName(IRI.create("http://example.com#123ABC")), is(Optional.of("123ABC")));
    }


}
