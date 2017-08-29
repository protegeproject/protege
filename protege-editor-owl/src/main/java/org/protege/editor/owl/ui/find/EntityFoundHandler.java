package org.protege.editor.owl.ui.find;

import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
@FunctionalInterface
public interface EntityFoundHandler {

    void handleChosenEntity(@Nonnull OWLEntity entity);
}
