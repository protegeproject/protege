package org.protege.editor.owl.model.axiom;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

/**
 * Tests subject definition extraction for handled and unhandled OWL objects.
 */
public class DefaultSubjectDefinitionExtractor_TestCase {

    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    private final OWLDataFactory dataFactory = manager.getOWLDataFactory();

    private DefaultSubjectDefinitionExtractor extractor;

    private OWLOntology ontology;

    @Before
    public void setUp() throws OWLOntologyCreationException {
        extractor = new DefaultSubjectDefinitionExtractor();
        ontology = manager.createOntology();
    }

    @Test
    public void shouldReturnDefiningAxiomsForClass() {
        OWLClass cls = dataFactory.getOWLClass(IRI.create("http://example.org/A"));
        OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(cls, dataFactory.getOWLThing());
        manager.addAxiom(ontology, axiom);

        Set<OWLAxiom> result = extractor.getDefiningAxioms(cls, ontology);

        assertThat(result, contains(axiom));
    }

    @Test
    public void shouldReturnEmptySetForUnhandledObject() {
        Set<OWLAxiom> result = extractor.getDefiningAxioms(dataFactory.getOWLLiteral("value"), ontology);

        assertThat(result, empty());
    }
}
