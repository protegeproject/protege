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
 * 2019-04-23
 */
public class IdPolicyParser_GO_IT {

    private OWLOntology ontology;

    private IdRangePolicy idRangePolicy;

    @Before
    public void setUp() throws Exception {
        InputStream is = IdPolicyParser_GO_IT.class.getResourceAsStream("/idpolicy/go-idranges.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(is);
        IdRangePolicyParser policyParser = IdRangePolicyParser.get(ontology);
        idRangePolicy = policyParser.parse();
    }

    @Test
    public void shouldParsePolicyIdFor() {
        assertThat(idRangePolicy.getIdPolicyFor(), is("GO"));
    }

    @Test
    public void shouldParseIdDigitCount() {
        assertThat(idRangePolicy.getIdDigitCount(), is(7));
    }

    @Test
    public void shouldParseIdPrefix() {
        assertThat(idRangePolicy.getIdPrefix(), is("http://purl.obolibrary.org/obo/GO_"));
    }

    @Test
    public void shouldParseUserRanges() {
        assertThat(idRangePolicy.getUserIdRanges(), hasSize(24));
    }

    @Test
    public void shouldParseUserIdRange() {
        UserIdRange userRange = getUserIdRangeForDavidHill();
        assertThat(userRange.getUserId(), is("David Hill"));
    }

    @Test
    public void shouldParseUserNumericRange() {
        UserIdRange userRange = getUserIdRangeForDavidHill();
        IdRange idRange = userRange.getIdRange();
        assertThat(idRange.getLowerBound(), is(60001));
        assertThat(idRange.getUpperBound(), is(65000));
    }

    private UserIdRange getUserIdRangeForDavidHill() {
        return idRangePolicy
                    .getUserIdRanges()
                    .stream()
                    .filter(idRange -> idRange.getUserId().equals("David Hill"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Could not find expected user range"));
    }
}
