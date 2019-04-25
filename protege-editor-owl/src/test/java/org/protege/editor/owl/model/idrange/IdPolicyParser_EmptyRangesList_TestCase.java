package org.protege.editor.owl.model.idrange;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public class IdPolicyParser_EmptyRangesList_TestCase {

    private OWLOntology ontology;

    @Before
    public void setUp() throws Exception {
        InputStream is = IdPolicyParser_GO_TestCase.class.getResourceAsStream("/idpolicy/empty-idranges.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(is);
    }

    @Test
    public void shouldParseGOIdPolicy() {
        IdRangesPolicyParser policyParser = IdRangesPolicyParser.get(ontology);
        IdRangesPolicy idRangesPolicy = policyParser.parse();
        assertThat(idRangesPolicy.getIdPolicyFor(), is("GO"));
        assertThat(idRangesPolicy.getIdDigitCount(), is(7));
        assertThat(idRangesPolicy.getIdPrefix(), is("http://purl.obolibrary.org/obo/GO_"));
        assertThat(idRangesPolicy.getUserIdRanges(), hasSize(0));
    }
}
