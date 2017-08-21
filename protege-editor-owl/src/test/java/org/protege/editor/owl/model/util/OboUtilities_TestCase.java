package org.protege.editor.owl.model.util;

import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2017
 */
public class OboUtilities_TestCase {

    @Test
    public void shouldRecogniseAsOboId() {
        assertThat(OboUtilities.isOboId("GO:0001234"), is(true));
    }

    @Test
    public void shouldRecogniseWithUnderscoreAsOboId() {
        assertThat(OboUtilities.isOboId("OTHER_THING:0001234"), is(true));
    }

    @Test
    public void shouldNotRecogniseAsOboId() {
        assertThat(OboUtilities.isOboId("owl:Thing"), is(false));
    }

    @Test
    public void shouldParseOboId() {
        IRI iri = OboUtilities.getIriFromOboId("GO:0001234");
        assertThat(iri.toString(), is("http://purl.obolibrary.org/obo/GO_0001234"));
    }

    @Test
    public void shouldParseOboIdWithUnderscore() {
        IRI iri = OboUtilities.getIriFromOboId("OTHER_THING:0001234");
        assertThat(iri.toString(), is("http://purl.obolibrary.org/obo/OTHER_THING_0001234"));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowRuntimeException() {
        OboUtilities.getIriFromOboId("owl:Thing");
    }
}
