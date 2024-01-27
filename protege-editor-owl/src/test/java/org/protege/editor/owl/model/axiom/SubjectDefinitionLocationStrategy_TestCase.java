package org.protege.editor.owl.model.axiom;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.owl.model.HasActiveOntology;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
@RunWith(MockitoJUnitRunner.class)
public class SubjectDefinitionLocationStrategy_TestCase {

    @Mock
    private OWLEntity subject;

    @Mock
    private OWLAxiom axiom;

    @Mock
    private OWLOntology ontologyA;

    @Mock
    private OWLOntology ontologyB;

    @Mock
    private OWLOntology ontologyC;

    @Mock
    private TopologicallySortedImportsClosureProvider importsClosureProvider;

    @Mock
    private SubjectDefinitionExtractor subjectDefinitionExtractor;

    @Mock
    private AxiomSubjectProvider subjectProvider;

    @Mock
    private HasActiveOntology hasActiveOntology;

    @Before
    public void setUp() {
        // OntologyA imports OntologyB which imports OntologyC
        when(importsClosureProvider.getTopologicallySortedImportsClosure(ontologyA)).thenReturn(
                Lists.newArrayList(ontologyA, ontologyB, ontologyC)
        );

        // The subject of our axiom is subject
        when(subjectProvider.getAxiomSubject(axiom))
                .thenReturn(Optional.<OWLObject>of(subject));

        // Active is the root ontology i.e. OntologyA
        when(hasActiveOntology.getActiveOntology()).thenReturn(ontologyA);
    }

    @Test
    public void shouldReturnActiveOntologyIfAxiomDoesNotHaveASubject() {
        SubjectDefinitionLocationStrategy strategy = new SubjectDefinitionLocationStrategy(
                importsClosureProvider,
                subjectProvider,
                subjectDefinitionExtractor

        );
        when(subjectProvider.getAxiomSubject(any(OWLAxiom.class))).thenReturn(Optional.<OWLObject>absent());
        OWLOntology ontology = strategy.getFreshAxiomLocation(axiom, hasActiveOntology);
        assertThat(ontology, is(equalTo(ontologyA)));
    }

    @Test
    public void shouldReturnActiveOntologyIfNoOntologyContainsSubjectDefiningAxiom() {
        // OntologyA does not define subject
        when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyA))
                .thenReturn(Collections.<OWLAxiom>emptySet());

        // OntologyB does not define subject
        when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyB))
                .thenReturn(Collections.<OWLAxiom>emptySet());

        // OntologyC defines subject
        when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyC))
                .thenReturn(Collections.<OWLAxiom>emptySet());

        SubjectDefinitionLocationStrategy strategy = new SubjectDefinitionLocationStrategy(
                importsClosureProvider,
                subjectProvider,
                subjectDefinitionExtractor

        );

        OWLOntology ontology = strategy.getFreshAxiomLocation(axiom, hasActiveOntology);
        assertThat(ontology, is(equalTo(ontologyA)));
    }

    @Test
    public void shouldOntologyInImportsClosureThatContainsDefiningAxiom() {
        // OntologyA does not define subject
        when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyA))
                .thenReturn(Collections.<OWLAxiom>emptySet());

        // OntologyB does not define subject
        when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyB))
                .thenReturn(Collections.<OWLAxiom>emptySet());

        // Ontology c defines subject
        when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyC))
                .thenReturn(Collections.singleton(axiom));

        SubjectDefinitionLocationStrategy strategy = new SubjectDefinitionLocationStrategy(
                importsClosureProvider,
                subjectProvider,
                subjectDefinitionExtractor

        );

        OWLOntology ontology = strategy.getFreshAxiomLocation(axiom, hasActiveOntology);
        assertThat(ontology, is(equalTo(ontologyC)));
    }

    @Test
    public void shouldReturnDeepestOntologyInImportsClosureThatContainsDefiningAxiom2() {
        // OntologyA does not define subject
        when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyA))
                .thenReturn(Collections.<OWLAxiom>emptySet());

        // OntologyB defines subject
        when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyB))
                .thenReturn(Collections.singleton(axiom));

        // Ontology C defines subject
        //when(subjectDefinitionExtractor.getDefiningAxioms(subject, ontologyC))
        //        .thenReturn(Collections.singleton(axiom));

        SubjectDefinitionLocationStrategy strategy = new SubjectDefinitionLocationStrategy(
                importsClosureProvider,
                subjectProvider,
                subjectDefinitionExtractor

        );

        OWLOntology ontology = strategy.getFreshAxiomLocation(axiom, hasActiveOntology);
        assertThat(ontology, is(equalTo(ontologyB)));
    }
}
