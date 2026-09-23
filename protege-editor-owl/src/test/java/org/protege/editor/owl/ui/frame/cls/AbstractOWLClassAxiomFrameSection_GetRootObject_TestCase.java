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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests the behaviour that must be preserved when the unused anonymous defined class support
 * is removed in #1349. With no anonymous defined class manager registered, the section should
 * return the root object supplied by its frame unchanged.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AbstractOWLClassAxiomFrameSection_GetRootObject_TestCase {

    @Mock
    private OWLEditorKit editorKit;

    @Mock
    private OWLModelManager modelManager;

    @Mock
    private OWLFrame<OWLClassExpression> frame;

    private OWLDataFactory df;

    private OWLClass namedClass;

    private AbstractOWLClassAxiomFrameSection<OWLAxiom, Object> section;

    @Before
    public void setUp() {
        df = OWLManager.getOWLDataFactory();
        namedClass = df.getOWLClass(IRI.create("http://example.com/frame#A"));

        when(editorKit.getModelManager()).thenReturn(modelManager);
        // As in a running Protégé: nothing has registered an anonymous defined class manager.
        when(modelManager.get(anyString())).thenReturn(null);

        section = new AbstractOWLClassAxiomFrameSection<OWLAxiom, Object>(editorKit, "Test", frame) {
            @Override
            protected void addAxiom(OWLAxiom ax, OWLOntology ont) {
            }

            @Override
            protected Set<OWLAxiom> getClassAxioms(OWLClassExpression descr, OWLOntology ont) {
                return Collections.emptySet();
            }

            @Override
            protected OWLAxiom createAxiom(Object object) {
                return null;
            }

            @Override
            protected void clear() {
            }

            @Override
            public org.protege.editor.owl.ui.editor.OWLObjectEditor<Object> getObjectEditor() {
                return null;
            }

            @Override
            public java.util.Comparator<org.protege.editor.owl.ui.frame.OWLFrameSectionRow<OWLClassExpression, OWLAxiom, Object>> getRowComparator() {
                return null;
            }
        };
    }

    /**
     * Verifies that a named class supplied by the frame is returned unchanged.
     */
    @Test
    public void shouldReturnTheNamedClassTheFrameSupplies() {
        when(frame.getRootObject()).thenReturn(namedClass);

        assertSame(namedClass, section.getRootObject());
    }

    /**
     * Verifies that an anonymous class expression is returned unchanged.
     */
    @Test
    public void shouldReturnAnAnonymousClassExpressionUnchanged() {
        OWLClassExpression anonymous = df.getOWLObjectIntersectionOf(
                namedClass, df.getOWLClass(IRI.create("http://example.com/frame#B")));
        when(frame.getRootObject()).thenReturn(anonymous);

        assertSame(anonymous, section.getRootObject());
    }

    /**
     * Verifies that a missing frame root is represented by {@code null}.
     */
    @Test
    public void shouldReturnNullWhenTheFrameHasNoRoot() {
        when(frame.getRootObject()).thenReturn(null);

        assertNull(section.getRootObject());
    }

}
