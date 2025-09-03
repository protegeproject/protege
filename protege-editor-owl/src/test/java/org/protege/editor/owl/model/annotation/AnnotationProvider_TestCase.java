package org.protege.editor.owl.model.annotation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataFactory;

import javax.inject.Provider;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 14 Jun 16
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationProvider_TestCase {

    private AnnotationProvider provider;

    @Mock
    private Provider<IRI> propertyProvider;

    @Mock
    private AnnotationValueProvider valueProvider;

    @Mock
    private OWLDataFactory dataFactory;

    @Mock
    private OWLAnnotationValue value;

    @Mock
    private OWLAnnotation annotation;

    @Before
    public void setUp() throws Exception {
        provider = new AnnotationProvider(propertyProvider, valueProvider);
        IRI propertyIRI = mock(IRI.class);
        when(propertyProvider.get()).thenReturn(propertyIRI);
        when(dataFactory.getOWLAnnotation(any(), any())).thenReturn(annotation);
    }

    @Test
    public void shouldReturnAnnotationForPresentValue() {
        when(valueProvider.getAnnotationValue(dataFactory)).thenReturn(Optional.of(value));
        assertThat(provider.getAnnotation(dataFactory), is(Optional.of(annotation)));
    }

    @Test
    public void shouldReturnAbsentIfValueIsAbsent() throws Exception {
        when(valueProvider.getAnnotationValue(dataFactory)).thenReturn(Optional.<OWLAnnotationValue>empty());
        assertThat(provider.getAnnotation(dataFactory), is(Optional.empty()));
    }
}
