package org.protege.editor.owl.model.util;

import org.junit.Before;
import org.junit.Test;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the visitor implementations used by OWL model utility classes.
 */
public class VisitorImplementations_TestCase {

    private final OWLDataFactory dataFactory = OWLManager.getOWLDataFactory();

    private OWLClass a;

    private OWLClass b;

    private OWLObjectProperty p;

    private OWLObjectProperty q;

    @Before
    public void setUp() {
        a = dataFactory.getOWLClass(IRI.create("http://example.org/A"));
        b = dataFactory.getOWLClass(IRI.create("http://example.org/B"));
        p = dataFactory.getOWLObjectProperty(IRI.create("http://example.org/p"));
        q = dataFactory.getOWLObjectProperty(IRI.create("http://example.org/q"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateCoveringAxiomAndIgnoreUnhandledExpression() {
        OWLObjectHierarchyProvider<OWLClass> hierarchyProvider = mock(OWLObjectHierarchyProvider.class);
        when(hierarchyProvider.getChildren(a)).thenReturn(Collections.singleton(b));
        CoveringAxiomFactory factory = new CoveringAxiomFactory(dataFactory, hierarchyProvider);
        a.accept(factory);
        dataFactory.getOWLObjectSomeValuesFrom(p, b).accept(factory);

        assertThat(factory.getCoveringAxiom(), is(dataFactory.getOWLObjectUnionOf(b)));
    }

    @Test
    public void shouldExtractSomeValuesFromAndHasValueFillers() {
        ObjectSomeValuesFromFillerExtractor extractor = new ObjectSomeValuesFromFillerExtractor(dataFactory, p);
        OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(IRI.create("http://example.org/i"));
        dataFactory.getOWLObjectSomeValuesFrom(p, dataFactory.getOWLObjectIntersectionOf(a, b)).accept(extractor);
        dataFactory.getOWLObjectHasValue(p, individual).accept(extractor);
        dataFactory.getOWLObjectSomeValuesFrom(q, a).accept(extractor);
        dataFactory.getOWLObjectAllValuesFrom(p, a).accept(extractor);

        assertThat(extractor.getFillers(), containsInAnyOrder(
                a,
                b,
                dataFactory.getOWLObjectOneOf(individual)));
    }

    @Test
    public void shouldExtractRestrictedPropertiesAndIgnoreNamedClasses() {
        RestrictedPropertyExtractor extractor = new RestrictedPropertyExtractor();
        OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(IRI.create("http://example.org/dp"));
        OWLClassExpression expression = dataFactory.getOWLObjectIntersectionOf(Arrays.asList(
                a,
                dataFactory.getOWLObjectSomeValuesFrom(p, b),
                dataFactory.getOWLObjectComplementOf(
                        dataFactory.getOWLDataSomeValuesFrom(dataProperty, dataFactory.getTopDatatype()))));

        expression.accept(extractor);

        Set<OWLPropertyExpression> properties = extractor.getRestrictedProperties();
        assertThat(properties, containsInAnyOrder(p, dataProperty));
    }

    @Test
    public void shouldLeaveResultsEmptyForUnhandledExpressions() {
        ObjectSomeValuesFromFillerExtractor fillerExtractor = new ObjectSomeValuesFromFillerExtractor(dataFactory, p);
        RestrictedPropertyExtractor propertyExtractor = new RestrictedPropertyExtractor();

        a.accept(fillerExtractor);
        a.accept(propertyExtractor);

        assertThat(fillerExtractor.getFillers(), empty());
        assertThat(propertyExtractor.getRestrictedProperties(), empty());
    }
}
