package org.protege.owlapi.inference.cls;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

/**
 * Tests the visitors that extract named classes and class hierarchy relationships.
 */
public class VisitorImplementations_TestCase {

    private final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();

    private OWLClass a;

    private OWLClass b;

    private OWLClass c;

    @Before
    public void setUp() {
        a = dataFactory.getOWLClass(IRI.create("http://example.org/A"));
        b = dataFactory.getOWLClass(IRI.create("http://example.org/B"));
        c = dataFactory.getOWLClass(IRI.create("http://example.org/C"));
    }

    @Test
    public void shouldExtractNamedIntersectionOperands() {
        NamedClassExtractor extractor = new NamedClassExtractor();

        dataFactory.getOWLObjectIntersectionOf(a, b).accept(extractor);

        assertThat(extractor.getResult(), containsInAnyOrder(a, b));
    }

    @Test
    public void shouldIgnoreUnhandledClassExpression() {
        NamedClassExtractor extractor = new NamedClassExtractor();

        dataFactory.getOWLObjectUnionOf(a, b).accept(extractor);

        assertThat(extractor.getResult(), empty());
    }

    @Test
    public void shouldFindNamedConjunct() {
        NamedConjunctChecker checker = new NamedConjunctChecker();

        assertThat(checker.containsConjunct(a, dataFactory.getOWLObjectIntersectionOf(a, b)), is(true));
        assertThat(checker.containsConjunct(a, dataFactory.getOWLObjectUnionOf(a, b)), is(false));
    }

    @Test
    public void shouldExtractChildFromSubclassAxiom() {
        ChildClassExtractor extractor = new ChildClassExtractor();
        extractor.setCurrentParentClass(b);

        dataFactory.getOWLSubClassOfAxiom(a, b).accept(extractor);
        dataFactory.getOWLDisjointClassesAxiom(a, c).accept(extractor);

        assertThat(extractor.getResult(), containsInAnyOrder(a));
    }

    @Test
    public void shouldExtractParentFromSubclassAxiom() {
        ParentClassExtractor extractor = new ParentClassExtractor();
        extractor.setCurrentClass(a);

        dataFactory.getOWLSubClassOfAxiom(a, dataFactory.getOWLObjectIntersectionOf(b, c)).accept(extractor);
        dataFactory.getOWLDisjointClassesAxiom(a, b).accept(extractor);

        assertThat(extractor.getResult(), containsInAnyOrder(b, c));
    }
}
