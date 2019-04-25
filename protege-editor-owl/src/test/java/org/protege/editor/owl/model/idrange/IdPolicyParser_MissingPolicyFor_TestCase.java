package org.protege.editor.owl.model.idrange;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.InputStream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public class IdPolicyParser_MissingPolicyFor_TestCase {

    private OWLOntology ontology;

    @Before
    public void setUp() throws Exception {
        InputStream is = IdPolicyParser_GO_TestCase.class.getResourceAsStream("/idpolicy/missing-policyfor-idranges.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(is);
    }

    @Test(expected = IdPolicyParseException.class)
    public void shouldFailParse() {
        IdRangesPolicyParser parser = IdRangesPolicyParser.get(ontology);
        parser.parse();
    }
}
