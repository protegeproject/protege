package org.protege.editor.owl.ui.renderer.context;

import org.semanticweb.owlapi.model.OWLClass;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 13 Sep 16
 */
public interface DefinedClassChecker {

    /**
     * Determines whether the specified class is a defined class.
     * @param cls The class.
     * @return true if the specified class is a defined class, otherwise false.
     */
    boolean isDefinedClass(@Nonnull OWLClass cls);
}
