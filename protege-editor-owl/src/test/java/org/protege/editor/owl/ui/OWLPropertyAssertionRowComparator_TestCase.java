package org.protege.editor.owl.ui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OWLPropertyAssertionRowComparator_TestCase {

    @Mock
    Comparator<OWLObject> delegate;
    @Mock
    OWLFrameSectionRow<OWLIndividual, OWLObjectPropertyAssertionAxiom, String> o1;
    @Mock
    OWLFrameSectionRow<OWLIndividual, OWLObjectPropertyAssertionAxiom, String> o2;
    @Mock
    OWLObjectPropertyAssertionAxiom opa1;
    @Mock
    OWLObjectPropertyAssertionAxiom opa2;
    @Mock
    OWLObjectProperty op1;
    @Mock
    OWLObjectProperty op2;
    @Mock
    OWLIndividual i1;
    @Mock
    OWLIndividual i2;

    OWLPropertyAssertionRowComparator<OWLObjectPropertyAssertionAxiom, String> comparator;

    @Before
    public void setUp() {
        comparator = new OWLPropertyAssertionRowComparator<>(delegate);
    }

    @Test
    public void testInferred1() {
        when(o1.isInferred()).thenReturn(true);
        when(o2.isInferred()).thenReturn(false);
        assertEquals(1, comparator.compare(o1, o2));
    }

    @Test
    public void testInferred2() {
        when(o1.isInferred()).thenReturn(false);
        when(o2.isInferred()).thenReturn(true);
        assertEquals(-1, comparator.compare(o1, o2));
    }

    @Test
    public void testDifferentProperty() {
        when(o1.getAxiom()).thenReturn(opa1);
        when(o2.getAxiom()).thenReturn(opa2);
        when(opa1.getProperty()).thenReturn(op1);
        when(opa2.getProperty()).thenReturn(op2);
        when(delegate.compare(op1, op2)).thenReturn(-2);
        assertEquals(-2, comparator.compare(o1, o2));
    }

    @Test
    public void testDifferentIndividuals() {
        when(o1.getAxiom()).thenReturn(opa1);
        when(o2.getAxiom()).thenReturn(opa2);
        when(opa1.getProperty()).thenReturn(op1);
        when(opa2.getProperty()).thenReturn(op2);
        when(opa1.getObject()).thenReturn(i1);
        when(opa2.getObject()).thenReturn(i2);
        when(delegate.compare(op1, op2)).thenReturn(0);
        when(delegate.compare(i1, i2)).thenReturn(-2);
        assertEquals(-2, comparator.compare(o1, o2));
    }

    @Test
    public void testSame() {
        when(o1.getAxiom()).thenReturn(opa1);
        when(o2.getAxiom()).thenReturn(opa2);
        when(opa1.getProperty()).thenReturn(op1);
        when(opa2.getProperty()).thenReturn(op2);
        when(opa1.getObject()).thenReturn(i1);
        when(opa2.getObject()).thenReturn(i2);
        when(delegate.compare(op1, op2)).thenReturn(0);
        when(delegate.compare(i1, i2)).thenReturn(0);
        assertEquals(0, comparator.compare(o1, o2));
    }

}