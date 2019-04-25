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
public class IdPolicyParser_GO_TestCase {

    private static final int EXPECTED_LOWER_BOUND = 60001;

    private static final int EXPECTED_UPPER_BOUND = 65000;

    private static final String EXPECTED_USER_ID = "David Hill";

    private static final String EXPECTED_ID_PREFIX = "http://purl.obolibrary.org/obo/GO_";

    private static final int EXPECTED_ID_RANGE_COUNT = 24;

    private static final int EXPECTED_ID_DIGIT_COUNT = 7;

    private static final String EXPECTED_OBO_NAMESPACE = "GO";

    private IdRangePolicy idRangePolicy;

    @Before
    public void setUp() throws Exception {
        InputStream is = IdPolicyParser_GO_TestCase.class.getResourceAsStream("/idpolicy/go-idranges.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(is);
        IdRangePolicyParser policyParser = IdRangePolicyParser.get(ontology);
        idRangePolicy = policyParser.parse();
    }

    @Test
    public void shouldParsePolicyIdFor() {
        assertThat(idRangePolicy.getIdPolicyFor(), is(EXPECTED_OBO_NAMESPACE));
    }

    @Test
    public void shouldParseIdDigitCount() {
        assertThat(idRangePolicy.getIdDigitCount(), is(EXPECTED_ID_DIGIT_COUNT));
    }

    @Test
    public void shouldParseIdPrefix() {
        assertThat(idRangePolicy.getIdPrefix(), is(EXPECTED_ID_PREFIX));
    }

    @Test
    public void shouldParseUserRanges() {
        assertThat(idRangePolicy.getUserIdRanges(), hasSize(EXPECTED_ID_RANGE_COUNT));
    }

    @Test
    public void shouldParseUserIdRange() {
        UserIdRange userRange = getUserIdRangeForDavidHill();
        assertThat(userRange.getUserId(), is(EXPECTED_USER_ID));
    }

    @Test
    public void shouldParseUserNumericRange() {
        UserIdRange userRange = getUserIdRangeForDavidHill();
        IdRange idRange = userRange.getIdRange();
        assertThat(idRange.getLowerBound(), is(EXPECTED_LOWER_BOUND));
        assertThat(idRange.getUpperBound(), is(EXPECTED_UPPER_BOUND));
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
