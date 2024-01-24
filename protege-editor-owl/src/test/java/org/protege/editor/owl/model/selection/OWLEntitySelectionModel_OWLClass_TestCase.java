package org.protege.editor.owl.model.selection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 10 Oct 2016
 */
@RunWith(MockitoJUnitRunner.class)
public class OWLEntitySelectionModel_OWLClass_TestCase {

    private OWLEntitySelectionModel selectionModel;

    @Mock
    private OWLSelectionModel delegate;

    @Mock
    private OWLClass cls;

    @Before
    public void setUp() {
        selectionModel = new OWLEntitySelectionModel(delegate);
    }

    @Test
    public void shouldReturnOptionalEmptyForNullObjectAndNullEntity() {
        when(delegate.getSelectedObject()).thenReturn(null);
        //when(delegate.getSelectedEntity()).thenReturn(null);
        assertThat(selectionModel.getSelectedClass(), is(Optional.empty()));
    }

    @Test
    public void shouldReturnOptionalEmptyForNullObjectAndOtherEntity() {
        when(delegate.getSelectedObject()).thenReturn(null);
        //when(delegate.getSelectedEntity()).thenReturn(mock(OWLEntity.class));
        assertThat(selectionModel.getSelectedClass(), is(Optional.empty()));
    }

    @Test
    public void shouldReturnOptionalEmptyForOtherObjectAndNullEntity() {
        when(delegate.getSelectedObject()).thenReturn(mock(OWLObject.class));
        when(delegate.getSelectedEntity()).thenReturn(null);
        assertThat(selectionModel.getSelectedClass(), is(Optional.empty()));
    }

    @Test
    public void shouldReturnOptionalEmptyForOtherObjectAndOtherEntity() {
        when(delegate.getSelectedObject()).thenReturn(mock(OWLObject.class));
        when(delegate.getSelectedEntity()).thenReturn(mock(OWLEntity.class));
        assertThat(selectionModel.getSelectedClass(), is(Optional.empty()));
    }

    @Test
    public void shouldReturnOptionalEmptyForNullEntityAndClass() {
        when(delegate.getSelectedObject()).thenReturn(null);
        //when(delegate.getSelectedEntity()).thenReturn(cls);
        assertThat(selectionModel.getSelectedClass(), is(Optional.empty()));
    }

    @Test
    public void shouldReturnOptionalOfClassForClassAndNullEntity() {
        when(delegate.getSelectedObject()).thenReturn(cls);
        //when(delegate.getSelectedEntity()).thenReturn(null);
        assertThat(selectionModel.getSelectedClass(), is(Optional.of(cls)));
    }

    @Test
    public void shouldReturnOptionalOfClassForOtherObjectAndClass() {
        when(delegate.getSelectedObject()).thenReturn(mock(OWLObject.class));
        when(delegate.getSelectedEntity()).thenReturn(cls);
        assertThat(selectionModel.getSelectedClass(), is(Optional.of(cls)));
    }

    @Test
    public void shouldReturnOptionalEmptyForOtherEntity() {
        OWLEntity otherEntity = mock(OWLEntity.class);
        when(delegate.getSelectedObject()).thenReturn(otherEntity);
        //when(delegate.getSelectedEntity()).thenReturn(otherEntity);
        assertThat(selectionModel.getSelectedClass(), is(Optional.empty()));
    }

    @Test
    public void shouldReturnOptionalOfClassForClassAndOtherEntity() {
        when(delegate.getSelectedObject()).thenReturn(cls);
        //when(delegate.getSelectedEntity()).thenReturn(mock(OWLEntity.class));
        assertThat(selectionModel.getSelectedClass(), is(Optional.of(cls)));
    }

    @Test
    public void shouldReturnOptionalOfClassForClassAndClass() {
        when(delegate.getSelectedObject()).thenReturn(cls);
        //when(delegate.getSelectedEntity()).thenReturn(cls);
        assertThat(selectionModel.getSelectedClass(), is(Optional.of(cls)));
    }






}
