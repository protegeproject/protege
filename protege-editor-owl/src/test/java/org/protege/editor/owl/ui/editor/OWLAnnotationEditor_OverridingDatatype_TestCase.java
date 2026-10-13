package org.protege.editor.owl.ui.editor;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Tests the choice of the datatype that the literal editor is locked to, given an annotation property's ranges.
 * See <a href="https://github.com/protegeproject/protege/issues/1111">issue 1111</a>.
 */
public class OWLAnnotationEditor_OverridingDatatype_TestCase {

    @Test
    public void shouldNotOverrideFor_rdfs_Literal() {
        assertThat(getOverridingDatatype(OWL2Datatype.RDFS_LITERAL.getIRI()), is(Optional.empty()));
    }

    @Test
    public void shouldNotOverrideFor_rdf_PlainLiteral() {
        assertThat(getOverridingDatatype(OWL2Datatype.RDF_PLAIN_LITERAL.getIRI()), is(Optional.empty()));
    }

    @Test
    public void shouldNotOverrideFor_rdf_langString() {
        assertThat(getOverridingDatatype(OWL2Datatype.RDF_LANG_STRING.getIRI()), is(Optional.empty()));
    }

    @Test
    public void shouldOverrideFor_xsd_string() {
        assertThat(getOverridingDatatype(OWL2Datatype.XSD_STRING.getIRI()), is(Optional.of(OWL2Datatype.XSD_STRING)));
    }

    @Test
    public void shouldNotOverrideForNonBuiltInRange() {
        assertThat(getOverridingDatatype(IRI.create("http://example.org/MyDatatype")), is(Optional.empty()));
    }

    @Test
    public void shouldNotOverrideForNoRanges() {
        assertThat(getOverridingDatatype(), is(Optional.empty()));
    }

    @Test
    public void shouldSkipRangeThatAdmitsLanguageTagsInFavourOfOneThatDoesNot() {
        assertThat(getOverridingDatatype(OWL2Datatype.RDFS_LITERAL.getIRI(), OWL2Datatype.XSD_STRING.getIRI()),
                   is(Optional.of(OWL2Datatype.XSD_STRING)));
    }

    private static Optional<OWL2Datatype> getOverridingDatatype(IRI... ranges) {
        return OWLAnnotationEditor.getOverridingDatatype(Stream.of(ranges));
    }
}
