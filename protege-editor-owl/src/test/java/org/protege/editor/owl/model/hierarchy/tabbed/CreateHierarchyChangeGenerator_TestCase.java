package org.protege.editor.owl.model.hierarchy.tabbed;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;

import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 Sep 16
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateHierarchyChangeGenerator_TestCase<E extends OWLEntity> {

    @Mock
    private OWLClass clsA, clsB, owlThing;

    @Mock
    private OWLDeclarationAxiom declarationForA;

    @Mock
    private HierarchyNodeCreator<OWLClass> hierarchyNodeCreator;

    @Mock
    private HierarchyAxiomProvider<OWLClass> hierarchyAxiomProvider;

    @Mock
    private OWLAxiom subClassOfAxiom;

    @Mock
    private OWLOntology targetOntology;

    @Mock
    private Edge edge;

    private List<OWLOntologyChange> changes;

    private CreateHierarchyChangeGenerator<OWLClass> generator;

    @Mock
    private OWLAxiom clsA_SubClassOf_clsB;

    @Mock
    private OWLAxiom clsA_SubClassOf_owlThing;

    @Before
    public void setUp() throws Exception {
        changes = new ArrayList<>();

        when(hierarchyNodeCreator.createEntity(Optional.empty(), changes)).thenReturn(owlThing);
        when(hierarchyNodeCreator.createEntity(Optional.of("A"), changes)).thenReturn(clsA);
        when(hierarchyNodeCreator.createEntity(Optional.of("B"), changes)).thenReturn(clsB);

        when(hierarchyAxiomProvider.getAxiom(clsA, clsB)).thenReturn(Optional.of(clsA_SubClassOf_clsB));
        when(hierarchyAxiomProvider.getAxiom(clsA, owlThing)).thenReturn(Optional.<OWLAxiom>empty());


        generator = new CreateHierarchyChangeGenerator<>(
                hierarchyNodeCreator,
                hierarchyAxiomProvider,
                targetOntology);
    }


    @Test
    public void shouldNotGenerateAxiomForEdgeToRoot() {
        when(edge.getParentName()).thenReturn(Optional.<String>empty());
        when(edge.getChildName()).thenReturn("A");

        Set<Edge> edges = Collections.singleton(edge);
        generator.generateHierarchy(edges, changes);
        assertThat(changes, not(hasItem(new AddAxiom(targetOntology, clsA_SubClassOf_owlThing))));
    }

    @Test
    public void shouldGenerateAxiomForEdge() {
        when(edge.getChildName()).thenReturn("A");
        when(edge.getParentName()).thenReturn(Optional.of("B"));

        Set<Edge> edges = Collections.singleton(edge);

        generator.generateHierarchy(edges, changes);
        assertThat(changes, hasItem(new AddAxiom(targetOntology, clsA_SubClassOf_clsB)));

    }
}
