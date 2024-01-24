package org.protege.editor.owl.model.inference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;

import java.util.Collections;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Aug 16
 */
@RunWith(MockitoJUnitRunner.class)
public class NoOpReasoner_TestCase {

    private NoOpReasoner reasoner;

    @Mock
    private OWLOntology ontology;

    @Mock
    private OWLOntologyManager manager;

    @Mock
    private OWLDataFactory dataFactory;

    @Mock
    private OWLClass owlThing, owlNothing;

    @Mock
    private OWLObjectProperty owlTopObjectProperty, owlBottomObjectProperty;

    @Mock
    private OWLDataProperty owlTopDataProperty, owlBottomDataProperty;


    @Before
    public void setUp() {
        //when(ontology.getOWLOntologyManager()).thenReturn(manager);
        //when(manager.getOWLDataFactory()).thenReturn(dataFactory);
        when(dataFactory.getOWLThing()).thenReturn(owlThing);
        when(dataFactory.getOWLNothing()).thenReturn(owlNothing);
        when(dataFactory.getOWLTopObjectProperty()).thenReturn(owlTopObjectProperty);
        when(dataFactory.getOWLBottomObjectProperty()).thenReturn(owlBottomObjectProperty);
        when(dataFactory.getOWLTopDataProperty()).thenReturn(owlTopDataProperty);
        when(dataFactory.getOWLBottomDataProperty()).thenReturn(owlBottomDataProperty);

        reasoner = new NoOpReasoner(ontology, dataFactory);
    }

    @Test
    public void shouldGetTopClassNode() {
        Node<OWLClass> node = reasoner.getTopClassNode();
        assertThat(node.getEntities(), is(Collections.singleton(owlThing)));
    }

    @Test
    public void shouldGetBottomClassNode() {
        Node<OWLClass> node = reasoner.getBottomClassNode();
        assertThat(node.getEntities(), is(Collections.singleton(owlNothing)));
    }

    @Test
    public void shouldGetTopObjectPropertyNode() {
        Node<OWLObjectPropertyExpression> node = reasoner.getTopObjectPropertyNode();
        assertThat(node.getEntities(), is(Collections.singleton(owlTopObjectProperty)));
    }

    @Test
    public void shouldGetBottomObjectPropertyNode() {
        Node<OWLObjectPropertyExpression> node = reasoner.getBottomObjectPropertyNode();
        assertThat(node.getEntities(), is(Collections.singleton(owlBottomObjectProperty)));
    }

    @Test
    public void shouldGetTopDataPropertyNode() {
        Node<OWLDataProperty> node = reasoner.getTopDataPropertyNode();
        assertThat(node.getEntities(), is(Collections.singleton(owlTopDataProperty)));
    }

    @Test
    public void shouldGetBottomDataPropertyNode() {
        Node<OWLDataProperty> node = reasoner.getBottomDataPropertyNode();
        assertThat(node.getEntities(), is(Collections.singleton(owlBottomDataProperty)));
    }

    @Test
    public void shouldHandleNullManager_For_GetTopClassNode() {
        try {
            //when(ontology.getOWLOntologyManager()).thenReturn(null);
            reasoner.getTopClassNode();
        } catch (NullPointerException e) {
            fail("NullPointerException");
        }
    }
    @Test
    public void shouldHandleNullManager_For_GetBottomClassNode() {
        try {
            //when(ontology.getOWLOntologyManager()).thenReturn(null);
            reasoner.getBottomClassNode();
        } catch (NullPointerException e) {
            fail("NullPointerException");
        }
    }
    @Test
    public void shouldHandleNullManager_For_GetTopObjectPropertyNode() {
        try {
            //when(ontology.getOWLOntologyManager()).thenReturn(null);
            reasoner.getTopObjectPropertyNode();
        } catch (NullPointerException e) {
            fail("NullPointerException");
        }
    }
    @Test
    public void shouldHandleNullManager_For_GetTopBottomObjectPropertyNode() {
        try {
            //when(ontology.getOWLOntologyManager()).thenReturn(null);
            reasoner.getBottomObjectPropertyNode();
        } catch (NullPointerException e) {
            fail("NullPointerException");
        }
    }
    @Test
    public void shouldHandleNullManager_For_GetTopDataPropertyNode() {
        try {
            //when(ontology.getOWLOntologyManager()).thenReturn(null);
            reasoner.getTopDataPropertyNode();
        } catch (NullPointerException e) {
            fail("NullPointerException");
        }
    }
    @Test
    public void shouldHandleNullManager_For_GetBottomObjectPropertyNode() {
        try {
            //when(ontology.getOWLOntologyManager()).thenReturn(null);
            reasoner.getBottomDataPropertyNode();
        } catch (NullPointerException e) {
            fail("NullPointerException");
        }
    }


}
