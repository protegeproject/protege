package org.protege.editor.owl.ui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests the ontology changes that are created for a new defined class.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class CreateDefinedClassPanel_TestCase {

    @Mock
    private OWLEditorKit editorKit;

    @Mock
    private OWLModelManager modelManager;

    private OWLDataFactory dataFactory;

    private OWLOntology ontology;

    @Before
    public void setUp() throws OWLOntologyCreationException {
        OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
        dataFactory = ontologyManager.getOWLDataFactory();
        ontology = ontologyManager.createOntology();
        when(editorKit.getOWLModelManager()).thenReturn(modelManager);
        when(modelManager.getOWLDataFactory()).thenReturn(dataFactory);
        when(modelManager.getActiveOntology()).thenReturn(ontology);
    }

    /**
     * Verifies that the class definition is appended without losing the original creation changes.
     */
    @Test
    public void shouldAppendTheClassDefinition() {
        OWLClass cls = dataFactory.getOWLClass(IRI.create("http://example.com/test#A"));
        OWLClassExpression definition = dataFactory.getOWLObjectSomeValuesFrom(
                dataFactory.getOWLObjectProperty(IRI.create("http://example.com/test#p")),
                dataFactory.getOWLThing());
        AddAxiom declarationChange = new AddAxiom(
                ontology, dataFactory.getOWLDeclarationAxiom(cls));
        OWLEntityCreationSet<OWLClass> creationSet = new OWLEntityCreationSet<>(
                cls, Collections.singletonList(declarationChange));

        OWLEntityCreationSet<OWLClass> result =
                CreateDefinedClassPanel.appendDefinitionToCreationSet(
                        creationSet, definition, editorKit);

        List<? extends OWLOntologyChange> changes = result.getOntologyChanges();
        assertSame(cls, result.getOWLEntity());
        assertEquals(2, changes.size());
        assertSame(declarationChange, changes.get(0));
        assertTrue(changes.get(1) instanceof AddAxiom);
        assertSame(ontology, changes.get(1).getOntology());
        assertEquals(
                dataFactory.getOWLEquivalentClassesAxiom(cls, definition),
                changes.get(1).getAxiom());
        assertEquals(1, creationSet.getOntologyChanges().size());
    }
}
