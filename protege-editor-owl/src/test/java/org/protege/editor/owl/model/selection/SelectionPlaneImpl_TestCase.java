package org.protege.editor.owl.model.selection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.protege.editor.core.util.HandlerRegistration;
import org.semanticweb.owlapi.model.OWLObject;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Aug 16
 */
@RunWith(MockitoJUnitRunner.class)
public class SelectionPlaneImpl_TestCase {

    private SelectionPlaneImpl selectionPlane;

    @Mock
    private OWLSelectionModel selectionModel;

    @Mock
    private SelectionDriver selectionDriverA, selectionDriverB;

    @Mock
    private Component componentA, componentB;

    @Mock
    private OWLObject selectionA, selectionB;

    @Before
    public void setUp() {
        selectionPlane = new SelectionPlaneImpl(selectionModel);

        when(selectionDriverA.getSelection()).thenReturn(Optional.of(selectionA));
        when(selectionDriverA.asComponent()).thenReturn(componentA);

        when(selectionDriverB.getSelection()).thenReturn(Optional.of(selectionB));
        when(selectionDriverB.asComponent()).thenReturn(componentB);
    }


    /**
     * Tests that a hierarchy listener is added when a driver is registered.
     */
    @Test
    public void shouldAddHierarchyListenerDuringRegistration() {
        selectionPlane.registerSelectionDriver(selectionDriverA);
        verify(componentA, times(1)).addHierarchyListener(any());
    }

    /**
     * Tests that drivers that are registered multiple times are in fact only registered once.
     *
     * The expected result is that a hierarchy listener is only added to the component once.
     */
    @Test
    public void shouldIgnoreRegistrationsOfAlreadyRegisterdDrivers() {
        selectionPlane.registerSelectionDriver(selectionDriverA);
        selectionPlane.registerSelectionDriver(selectionDriverA);
        verify(componentA, times(1)).addHierarchyListener(any());
    }

    /**
     * Tests that hierarchy listeners are removed when a driver is unregistered
     */
    @Test
    public void shouldRemoveHierarchyListenerOnRemoveRegistration() {
        HandlerRegistration registration = selectionPlane.registerSelectionDriver(selectionDriverA);
        ArgumentCaptor<HierarchyListener> listener = ArgumentCaptor.forClass(HierarchyListener.class);
        verify(componentA, times(1)).addHierarchyListener(listener.capture());
        registration.removeHandler();
        verify(componentA, times(1)).removeHierarchyListener(listener.getValue());
    }

    @Test
    public void shouldTransmitSelectionIfShowing() {
        when(componentA.isShowing()).thenReturn(true);
        selectionPlane.registerSelectionDriver(selectionDriverA);
        triggerHierarchyChangeEvent(componentA);
        verify(selectionModel, times(1)).setSelectedObject(selectionA);
    }

    @Test
    public void shouldNotTransmitSelectionIfNotShowing() {
        when(componentA.isShowing()).thenReturn(false);
        selectionPlane.registerSelectionDriver(selectionDriverA);
        triggerHierarchyChangeEvent(componentA);
        verify(selectionModel, never()).setSelectedObject(any());
    }

    /**
     * Tests the case where no prior selection has been made (i.e. {@code SelectionDriver#transmitSelection} has never
     * been called) and where multiple drivers are showing.
     *
     * The expected result is that the first registered driver gets its selection transmitted.  Any other drivers
     * do not have their selection transmitted.
     */
    @Test
    public void shouldTransmitSelectionOfFirstRegisteredDriverIfShowingButNoPriorSelection() {
        selectionPlane.registerSelectionDriver(selectionDriverA);
        selectionPlane.registerSelectionDriver(selectionDriverB);
        when(componentA.isShowing()).thenReturn(true);
        when(componentB.isShowing()).thenReturn(true);
        triggerHierarchyChangeEvent(componentA);
        verify(selectionModel, times(1)).setSelectedObject(selectionA);
        verify(selectionModel, never()).setSelectedObject(selectionB);
    }

    /**
     * Tests the case where a prior selection has been made (i.e. {@code SelectionDriver#transmitSelection} has
     * been called) and where multiple drivers are showing.
     *
     * The expected result is that the driver to transmit the selection gets its selection transmitted again.  Any
     * other drivers do not have their selection transmitted.
     */
    @Test
    public void shouldTransmitSelectionOfLastDriverIfShowingWithPriorSelection() {
        selectionPlane.registerSelectionDriver(selectionDriverA);
        selectionPlane.registerSelectionDriver(selectionDriverB);
        when(componentA.isShowing()).thenReturn(true);
        when(componentB.isShowing()).thenReturn(true);
        selectionPlane.transmitSelection(selectionDriverB, selectionB);
        reset(selectionModel);
        triggerHierarchyChangeEvent(componentA);
        verify(selectionModel, never()).setSelectedObject(selectionA);
        verify(selectionModel, times(1)).setSelectedObject(selectionB);
    }

    /**
     * Tests the case when multiple hierarchy change events occur.  If the hierarchy events are fired from
     * multiple drivers but each firing results in the same state of driver visibility and selection state
     * then only one selection transmition should take place.
     */
    @Test
    public void shouldNotTransmitMultipleSelections() {
        selectionPlane.registerSelectionDriver(selectionDriverA);
        selectionPlane.registerSelectionDriver(selectionDriverB);
        when(componentA.isShowing()).thenReturn(true);
        triggerHierarchyChangeEvent(componentA);
        triggerHierarchyChangeEvent(componentB);
        verify(selectionModel, times(1)).setSelectedObject(any());
    }


    private static void triggerHierarchyChangeEvent(Component component) {
        // Capture the listener that is added
        ArgumentCaptor<HierarchyListener> listener = ArgumentCaptor.forClass(HierarchyListener.class);
        verify(component, times(1)).addHierarchyListener(listener.capture());
        // Now trigger the hierarchy event
        listener.getValue().hierarchyChanged(mock(HierarchyEvent.class));
    }
}
