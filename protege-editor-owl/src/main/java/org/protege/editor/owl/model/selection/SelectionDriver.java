package org.protege.editor.owl.model.selection;

import org.semanticweb.owlapi.model.OWLObject;

import java.awt.*;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Aug 16
 */
public interface SelectionDriver {

    Component asComponent();

    Optional<OWLObject> getSelection();
}
