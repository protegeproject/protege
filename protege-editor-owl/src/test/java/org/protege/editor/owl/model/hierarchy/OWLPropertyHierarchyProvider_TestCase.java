package org.protege.editor.owl.model.hierarchy;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests property visitor dispatch and the defaults used for unsupported entities.
 */
public class OWLPropertyHierarchyProvider_TestCase {

    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    private final OWLDataFactory dataFactory = manager.getOWLDataFactory();

    private OWLObjectHierarchyProvider<OWLObjectProperty> objectPropertyProvider;

    private OWLObjectHierarchyProvider<OWLDataProperty> dataPropertyProvider;

    private OWLObjectHierarchyProvider<OWLAnnotationProperty> annotationPropertyProvider;

    private OWLPropertyHierarchyProvider provider;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        objectPropertyProvider = mock(OWLObjectHierarchyProvider.class);
        dataPropertyProvider = mock(OWLObjectHierarchyProvider.class);
        annotationPropertyProvider = mock(OWLObjectHierarchyProvider.class);
        provider = new OWLPropertyHierarchyProvider(
                manager,
                objectPropertyProvider,
                dataPropertyProvider,
                annotationPropertyProvider);
    }

    @Test
    public void shouldUseDefaultsForNonPropertyEntity() {
        OWLClass cls = dataFactory.getOWLClass(IRI.create("http://example.org/A"));

        assertThat(provider.containsReference(cls), is(false));
        assertThat(provider.getUnfilteredChildren(cls), empty());
        assertThat(provider.getParents(cls), empty());
        assertThat(provider.getEquivalents(cls), empty());
    }

    @Test
    public void shouldDelegateObjectPropertyOperations() {
        OWLObjectProperty property = dataFactory.getOWLObjectProperty(IRI.create("http://example.org/p"));
        OWLObjectProperty relatedProperty = dataFactory.getOWLObjectProperty(IRI.create("http://example.org/q"));
        Set<OWLObjectProperty> result = Collections.singleton(relatedProperty);
        when(objectPropertyProvider.containsReference(property)).thenReturn(true);
        when(objectPropertyProvider.getChildren(property)).thenReturn(result);
        when(objectPropertyProvider.getParents(property)).thenReturn(result);
        when(objectPropertyProvider.getEquivalents(property)).thenReturn(result);

        assertThat(provider.containsReference(property), is(true));
        assertThat(provider.getUnfilteredChildren(property), is(Collections.<OWLEntity>singleton(relatedProperty)));
        assertThat(provider.getParents(property), is(Collections.<OWLEntity>singleton(relatedProperty)));
        assertThat(provider.getEquivalents(property), is(Collections.<OWLEntity>singleton(relatedProperty)));
    }

    @Test
    public void shouldDelegateDataAndAnnotationProperties() {
        OWLDataProperty dataProperty = dataFactory.getOWLDataProperty(IRI.create("http://example.org/dp"));
        OWLAnnotationProperty annotationProperty = dataFactory.getOWLAnnotationProperty(IRI.create("http://example.org/ap"));
        when(dataPropertyProvider.containsReference(dataProperty)).thenReturn(true);
        when(annotationPropertyProvider.containsReference(annotationProperty)).thenReturn(true);

        assertThat(provider.containsReference(dataProperty), is(true));
        assertThat(provider.containsReference(annotationProperty), is(true));
    }
}
