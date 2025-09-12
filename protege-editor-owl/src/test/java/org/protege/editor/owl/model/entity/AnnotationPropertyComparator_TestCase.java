package org.protege.editor.owl.model.entity;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import java.util.Comparator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 28/05/2014
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class AnnotationPropertyComparator_TestCase {

    @Mock
    private OWLAnnotationProperty propertyA;

    @Mock
    private IRI iriA;

    @Mock
    private OWLAnnotationProperty propertyB;

    @Mock
    private IRI iriB;

    @Mock
    private Comparator<OWLAnnotationProperty> delegate;

    @Mock
    private ImmutableList<IRI> ordering;


    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfOrderingIsNull() {
        new AnnotationPropertyComparator(null, delegate);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerExceptionIfComparatorIsNull() {
        new AnnotationPropertyComparator(ordering, null);
    }

    @Before
    public void setUp() {
        when(propertyA.getIRI()).thenReturn(iriA);
        when(propertyB.getIRI()).thenReturn(iriB);
        // Always return propertyA before propertyB with the delegate
        when(delegate.compare(propertyA, propertyB)).thenReturn(-1);
    }

    @Test
    public void shouldCompareNonListedAnnotationPropertiesUsingDelegate() {
        when(ordering.indexOf(iriA)).thenReturn(-1);
        when(ordering.indexOf(iriB)).thenReturn(-1);
        AnnotationPropertyComparator comparator = new AnnotationPropertyComparator(ordering, delegate);
        int result = comparator.compare(propertyA, propertyB);
        assertThat(result, is(-1));
        verify(delegate, times(1)).compare(propertyA, propertyB);
    }

    @Test
    public void shouldReturnOrderedPropertyFirstWhenSecondPropertyIsNotInOrdering() {
        when(ordering.indexOf(iriA)).thenReturn(1);
        when(ordering.indexOf(iriB)).thenReturn(-1);
        AnnotationPropertyComparator comparator = new AnnotationPropertyComparator(ordering, delegate);
        int result = comparator.compare(propertyA, propertyB);
        assertThat(result, is(lessThan(0)));
        // Shouldn't use the delegate
        verify(delegate, never()).compare(propertyA, propertyB);
    }

    @Test
    public void shouldRespectOrderingWhenBothAreInList() {
        when(ordering.indexOf(iriA)).thenReturn(7);
        when(ordering.indexOf(iriB)).thenReturn(5);
        AnnotationPropertyComparator comparator = new AnnotationPropertyComparator(ordering, delegate);
        int result = comparator.compare(propertyA, propertyB);
        assertThat(result, is(greaterThan(0)));
        // Shouldn't use the delegate
        verify(delegate, never()).compare(propertyA, propertyB);
    }
}
