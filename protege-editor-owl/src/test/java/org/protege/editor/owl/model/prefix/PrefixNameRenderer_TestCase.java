package org.protege.editor.owl.model.prefix;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.XSDVocabulary;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 19 Oct 2017
 */
public class PrefixNameRenderer_TestCase {

    private PrefixedNameRenderer.Builder builder;

    @Before
    public void setUp() throws Exception {
        builder = PrefixedNameRenderer.builder();
    }

    @Test
    public void shouldRenderPrefixedName() {
        builder.withPrefix("test:", "http://test.com/test/");
        PrefixedNameRenderer renderer = builder.build();
        assertThat(renderer.getPrefixedNameOrQuotedIri(IRI.create("http://test.com/test/A")), is("test:A"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAddPrefixWithMissingColon() {
        builder.withPrefix("test", "http://test.com/test/");
    }

    @Test
    public void shouldAddOwlPrefix() {
        builder.withOwlPrefixes();
        PrefixedNameRenderer renderer = builder.build();
        assertThat(renderer.getPrefixedNameOrQuotedIri(OWLRDFVocabulary.OWL_THING.getIRI()), is("owl:Thing"));
    }

    @Test
    public void shouldAddRdfsPrefix() {
        builder.withOwlPrefixes();
        PrefixedNameRenderer renderer = builder.build();
        assertThat(renderer.getPrefixedNameOrQuotedIri(OWLRDFVocabulary.RDFS_LABEL.getIRI()), is("rdfs:label"));
    }

    @Test
    public void shouldAddRdfPrefix() {
        builder.withOwlPrefixes();
        PrefixedNameRenderer renderer = builder.build();
        assertThat(renderer.getPrefixedNameOrQuotedIri(OWLRDFVocabulary.RDF_TYPE.getIRI()), is("rdf:type"));
    }

    @Test
    public void shouldAddXsdPrefix() {
        builder.withOwlPrefixes();
        PrefixedNameRenderer renderer = builder.build();
        assertThat(renderer.getPrefixedNameOrQuotedIri(XSDVocabulary.STRING.getIRI()), is("xsd:string"));
    }

    @Test
    public void shouldAddWellKnownPrefixes() {
        builder.withWellKnownPrefixes();
        PrefixedNameRenderer renderer = builder.build();
        Arrays.stream(Namespaces.values())
              .map(ns -> ns.getPrefixIRI() + "A")
              .map(IRI::create)
              .forEach(iri -> assertThat(renderer.getPrefixedNameOrQuotedIri(iri), is(not(iri.toQuotedString()))));
    }

    @Test
    public void shouldAcceptEqualLengthPrefixes() {
        builder.withPrefix("a:", "http://test.com/a/");
        builder.withPrefix("b:", "http://test.com/b/");
        PrefixedNameRenderer renderer = builder.build();
        assertThat(renderer.getPrefixedNameOrQuotedIri(IRI.create("http://test.com/a/A")), is("a:A"));
        assertThat(renderer.getPrefixedNameOrQuotedIri(IRI.create("http://test.com/b/B")), is("b:B"));
    }

    @Test
    public void shouldRenderQuotedIri() {
        PrefixedNameRenderer renderer = builder.build();
        assertThat(renderer.getPrefixedNameOrQuotedIri(IRI.create("http://test.com/a/A")), is("<http://test.com/a/A>"));
    }
}
