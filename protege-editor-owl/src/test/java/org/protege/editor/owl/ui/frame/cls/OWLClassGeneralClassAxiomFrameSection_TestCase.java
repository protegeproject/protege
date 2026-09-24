package org.protege.editor.owl.ui.frame.cls;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

/**
 * Tests which axiom changes cause the general class axioms section to be reset.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class OWLClassGeneralClassAxiomFrameSection_TestCase {

    @Mock
    private OWLEditorKit editorKit;

    @Mock
    private OWLModelManager modelManager;

    @Mock
    private OWLFrame<OWLClass> frame;

    private final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

    private final OWLDataFactory dataFactory = manager.getOWLDataFactory();

    private OWLClass root;

    private OWLClassGeneralClassAxiomFrameSection section;

    private OWLOntology ontology;

    @Before
    public void setUp() throws OWLOntologyCreationException {
        root = dataFactory.getOWLClass(IRI.create("http://example.org/A"));
        when(editorKit.getModelManager()).thenReturn(modelManager);
        when(frame.getRootObject()).thenReturn(root);
        section = new OWLClassGeneralClassAxiomFrameSection(editorKit, frame);
        ontology = manager.createOntology();
    }

    @Test
    public void shouldReturnFalseForUnhandledAxiom() {
        AddAxiom change = new AddAxiom(ontology, dataFactory.getOWLDeclarationAxiom(root));

        assertThat(section.isResettingChange(change), is(false));
    }

    @Test
    public void shouldReturnTrueForGeneralSubclassAxiom() {
        OWLClass otherClass = dataFactory.getOWLClass(IRI.create("http://example.org/B"));
        AddAxiom change = new AddAxiom(ontology, dataFactory.getOWLSubClassOfAxiom(
                dataFactory.getOWLObjectIntersectionOf(root, otherClass),
                dataFactory.getOWLThing()));

        assertThat(section.isResettingChange(change), is(true));
    }
}
