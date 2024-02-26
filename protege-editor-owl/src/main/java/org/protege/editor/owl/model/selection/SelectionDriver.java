package org.protege.editor.owl.model.selection;

import java.awt.Component;
import java.util.Optional;

import org.semanticweb.owlapi.model.OWLObject;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Aug 16
 */
public interface SelectionDriver {

    Component asComponent();

    Optional<OWLObject> getSelection();
}
