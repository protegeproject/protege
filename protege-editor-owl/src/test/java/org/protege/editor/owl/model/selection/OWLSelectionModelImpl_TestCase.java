package org.protege.editor.owl.model.selection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.protege.editor.owl.model.util.OWLAxiomInstance;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 5 Aug 16
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class OWLSelectionModelImpl_TestCase {

    private OWLSelectionModelImpl selectionModel;

    private final OWLClass cls = new OWLClassImpl(IRI.create("http://some.class"));

    private final OWLObjectProperty objectProperty = new OWLObjectPropertyImpl(IRI.create("http://some.object.property"));

    private final OWLDataProperty dataProperty = new OWLDataPropertyImpl(IRI.create("http://some.data.property"));

    private final OWLAnnotationPropertyImpl annotationProperty = new OWLAnnotationPropertyImpl(IRI.create("http://some.anno.property"));

    private final OWLNamedIndividual namedIndividual = new OWLNamedIndividualImpl(IRI.create("http://some.individual"));

    private final OWLDatatype datatype = new OWLDatatypeImpl(IRI.create("http://some.datatype"));

    @Mock
    private OWLAxiomInstance axiomInstance;

    @Before
    public void setUp() {
        selectionModel = new OWLSelectionModelImpl();
    }

    @Test
    public void shouldGetLastSelectedClass() {
        selectionModel.setSelectedEntity(cls);
        OWLClass sel = selectionModel.getLastSelectedClass();
        assertThat(sel, is(cls));
    }

    @Test
    public void shouldNotGetLastSelectedClass() {
        OWLClass cls = selectionModel.getLastSelectedClass();
        assertThat(cls, is(nullValue()));
    }

    @Test
    public void shouldGetLastSelectedObjectProperty() {
        selectionModel.setSelectedEntity(objectProperty);
        OWLObjectProperty sel = selectionModel.getLastSelectedObjectProperty();
        assertThat(sel, is(objectProperty));
    }

    @Test
    public void shouldNotGetLastSelectedObjectProperty() {
        OWLObjectProperty p = selectionModel.getLastSelectedObjectProperty();
        assertThat(p, is(nullValue()));
    }

    @Test
    public void shouldGetLastSelectedDataProperty() {
        selectionModel.setSelectedEntity(dataProperty);
        OWLDataProperty sel = selectionModel.getLastSelectedDataProperty();
        assertThat(sel, is(dataProperty));
    }
    
    @Test
    public void shouldNotGetLastSelectedDataProperty() {
        OWLDataProperty p = selectionModel.getLastSelectedDataProperty();
        assertThat(p, is(nullValue()));
    }

    @Test
    public void shouldGetLastSelectedAnnotationProperty() {
        selectionModel.setSelectedEntity(annotationProperty);
        OWLAnnotationProperty sel = selectionModel.getLastSelectedAnnotationProperty();
        assertThat(sel, is(annotationProperty));
    }

    @Test
    public void shouldNotGetLastSelectedAnnotationProperty() {
        OWLAnnotationProperty p = selectionModel.getLastSelectedAnnotationProperty();
        assertThat(p, is(nullValue()));
    }

    @Test
    public void shouldGetLastSelectedNamedIndividual() {
        selectionModel.setSelectedEntity(namedIndividual);
        OWLNamedIndividual sel = selectionModel.getLastSelectedIndividual();
        assertThat(sel, is(namedIndividual));
    }


    @Test
    public void shouldNotGetLastSelectedNamedIndividual() {
        OWLNamedIndividual i = selectionModel.getLastSelectedIndividual();
        assertThat(i, is(nullValue()));
    }

    @Test
    public void shouldGetLastSelectedDatatype() {
        selectionModel.setSelectedEntity(datatype);
        OWLDatatype sel = selectionModel.getLastSelectedDatatype();
        assertThat(sel, is(datatype));
    }


    @Test
    public void shouldNotGetLastSelectedDatatype() {
        OWLDatatype d = selectionModel.getLastSelectedDatatype();
        assertThat(d, is(nullValue()));
    }

    @Test
    public void shouldGetLastSelectedAxiom() {
        selectionModel.setSelectedAxiom(axiomInstance);
        OWLAxiomInstance sel = selectionModel.getLastSelectedAxiomInstance();
        assertThat(sel, is(axiomInstance));
    }

    @Test
    public void shouldGetSelectedEntity() {
        selectionModel.setSelectedEntity(cls);
        assertThat(selectionModel.getSelectedEntity(), is(cls));
    }

    @Test
    public void shouldNotGetSelectedEntity() {
        assertThat(selectionModel.getSelectedEntity(), is(nullValue()));
    }


    @Test
    public void shouldGetSelectedObject() {
        selectionModel.setSelectedObject(cls);
        assertThat(selectionModel.getSelectedObject(), is(cls));
    }

    @Test
    public void shouldNotGetSelectedObject() {
        assertThat(selectionModel.getSelectedObject(), is(nullValue()));
    }

    @Test
    public void shouldNotThrowNullPointerExceptionOnSetSelectedEntityNull() {
        try {
            selectionModel.setSelectedEntity(null);
        } catch (NullPointerException e) {
            fail();
        }
    }

    @Test
    public void shouldNotifyListenerOnSelectionChange() throws Exception {
        OWLSelectionModelListener listener = mock(OWLSelectionModelListener.class);
        selectionModel.addListener(listener);
        selectionModel.setSelectedEntity(cls);
        verify(listener, times(1)).selectionChanged();
    }


    @Test
    public void shouldNotNotifyListenerOnSelectionNoOp() throws Exception {
        OWLSelectionModelListener listener = mock(OWLSelectionModelListener.class);
        selectionModel.addListener(listener);
        selectionModel.setSelectedEntity(cls);
        selectionModel.setSelectedEntity(cls);
        verify(listener, times(1)).selectionChanged();
    }


    @Test
    public void shouldNotNotifyRemovedListener() throws Exception {
        OWLSelectionModelListener listener = mock(OWLSelectionModelListener.class);
        selectionModel.addListener(listener);
        selectionModel.removeListener(listener);
        selectionModel.setSelectedEntity(cls);
        verify(listener, never()).selectionChanged();
    }

}
