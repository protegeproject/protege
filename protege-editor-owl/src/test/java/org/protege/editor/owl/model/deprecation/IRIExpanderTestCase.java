package org.protege.editor.owl.model.deprecation;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Aug 2017
 */
public class IRIExpanderTestCase {

    @Test
    public void shouldExpandOboId() {
        assertThat(IRIExpander.expand("GO:0001234"), is(Optional.of(IRI.create("http://purl.obolibrary.org/obo/GO_0001234"))));
    }

    @Test
    public void shouldExpandRdfsLabel() {
        assertThat(IRIExpander.expand("rdfs:label"), is(Optional.of(OWLRDFVocabulary.RDFS_LABEL.getIRI())));
    }

    @Test
    public void shouldExpandOther() {
        String otherIri = "http://other.com/other";
        assertThat(IRIExpander.expand(otherIri), is(Optional.of(IRI.create(otherIri))));
    }

    @Test
    public void shouldExpandSkos() {
        assertThat(IRIExpander.expand(SKOSVocabulary.DEFINITION.getPrefixedName()),
                   is(Optional.of(SKOSVocabulary.DEFINITION.getIRI())));
    }
}
