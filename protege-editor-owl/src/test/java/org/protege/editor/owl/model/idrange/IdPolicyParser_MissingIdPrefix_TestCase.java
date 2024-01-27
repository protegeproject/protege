package org.protege.editor.owl.model.idrange;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-25
 */
public class IdPolicyParser_MissingIdPrefix_TestCase {

    private OWLOntology ontology;

    @Before
    public void setUp() throws Exception {
        InputStream is = IdPolicyParser_GO_TestCase.class.getResourceAsStream("/idpolicy/missing-idprefix-idranges.owl");
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(is);
    }

    @Test(expected = IdRangesPolicyParseException.class)
    public void shouldFailParse() {
        IdRangesPolicyParser parser = IdRangesPolicyParser.get(ontology);
        parser.parse();
    }
}
