package org.protege.editor.owl.model.selection;

import org.protege.editor.core.util.HandlerRegistration;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Aug 16
 */
public interface SelectionPlane {


    /**
     * Register a SelectionDriver with this SelectionPlane.
     * @param driver The driver.
     * @return A HandlerRegistration that can be used to remove the driver.
     */
    HandlerRegistration registerSelectionDriver(SelectionDriver driver);

    /**
     * Transmit the selection.
     * @param driver The source of the selection.
     * @param selection The selection to be transmitted to the global selection model.
     */
    void transmitSelection(SelectionDriver driver, OWLObject selection);
}
