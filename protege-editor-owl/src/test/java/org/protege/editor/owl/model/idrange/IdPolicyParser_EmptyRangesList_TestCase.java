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
        IdRangePolicy idRangePolicy = policyParser.parse();
        assertThat(idRangePolicy.getIdPolicyFor(), is("GO"));
        assertThat(idRangePolicy.getIdDigitCount(), is(7));
        assertThat(idRangePolicy.getIdPrefix(), is("http://purl.obolibrary.org/obo/GO_"));
        assertThat(idRangePolicy.getUserIdRanges(), hasSize(0));
    }
}
