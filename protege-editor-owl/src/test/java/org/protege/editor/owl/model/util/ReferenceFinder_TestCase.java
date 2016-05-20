package org.protege.editor.owl.model.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 May 16
 */
@RunWith(MockitoJUnitRunner.class)
public class ReferenceFinder_TestCase {

    private ReferenceFinder referenceFinder;

    @Mock
    private OWLOntology ontology;

    @Mock
    private OWLAnnotationProperty entity;

    @Mock
    private OWLAxiom axiom;

    @Mock
    private OWLAnnotationAssertionAxiom annotationAssertionAxiom;

    @Mock
    private IRI iri;

    @Mock
    private OWLAnnotation ontologyAnnotation;

    @Before
    public void setUp() throws Exception {
        referenceFinder = new ReferenceFinder();
        when(entity.getIRI()).thenReturn(iri);
    }

    @Test
    public void shouldRetrieveNonAnnotationAssertionAxiom() {
        when(ontology.getReferencingAxioms(entity, Imports.EXCLUDED)).thenReturn(Collections.singleton(axiom));

        ReferenceFinder.ReferenceSet referenceSet = getReferenceSet();
        assertThat(referenceSet.getReferencingAxioms(), hasItem(axiom));
    }

    @Test
    public void shouldRetrieveAnnotationAssertionAxiomBySubjectReference() {
        when(ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION)).thenReturn(Collections.singleton(annotationAssertionAxiom));
        when(annotationAssertionAxiom.getSubject()).thenReturn(iri);
        when(annotationAssertionAxiom.getValue()).thenReturn(mock(IRI.class));

        ReferenceFinder.ReferenceSet referenceSet = getReferenceSet();
        assertThat(referenceSet.getReferencingAxioms(), hasItem(annotationAssertionAxiom));
    }

    @Test
    public void shouldRetrieveAnnotationAssertionAxiomByObjectReference() {
        when(ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION)).thenReturn(Collections.singleton(annotationAssertionAxiom));
        when(annotationAssertionAxiom.getValue()).thenReturn(iri);
        when(annotationAssertionAxiom.getSubject()).thenReturn(mock(IRI.class));

        ReferenceFinder.ReferenceSet referenceSet = getReferenceSet();
        assertThat(referenceSet.getReferencingAxioms(), hasItem(annotationAssertionAxiom));
    }

    @Test
    public void shouldRetrieveOntologyAnnotationsByValue() {
        when(ontology.getAnnotations()).thenReturn(Collections.singleton(ontologyAnnotation));
        when(ontologyAnnotation.getProperty()).thenReturn(mock(OWLAnnotationProperty.class));
        when(ontologyAnnotation.getValue()).thenReturn(iri);

        ReferenceFinder.ReferenceSet referenceSet = getReferenceSet();
        assertThat(referenceSet.getReferencingOntologyAnnotations(), hasItem(ontologyAnnotation));
    }

    @Test
    public void shouldRetrieveOntologyAnnotationsByProperty() {
        when(ontology.getAnnotations()).thenReturn(Collections.singleton(ontologyAnnotation));
        when(ontologyAnnotation.getProperty()).thenReturn(entity);
        when(ontologyAnnotation.getValue()).thenReturn(mock(IRI.class));

        ReferenceFinder.ReferenceSet referenceSet = getReferenceSet();
        assertThat(referenceSet.getReferencingOntologyAnnotations(), hasItem(ontologyAnnotation));
    }

    /**
     * Convenience method to get the ReferenceSet for the entity an ontology.
     * @return  The ReferenceSet.
     */
    private ReferenceFinder.ReferenceSet getReferenceSet() {
        return referenceFinder.getReferenceSet(Collections.singleton(entity), ontology);
    }
}
