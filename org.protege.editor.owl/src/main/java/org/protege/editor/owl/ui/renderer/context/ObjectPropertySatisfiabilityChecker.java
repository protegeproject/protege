package org.protege.editor.owl.ui.renderer.context;

import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 * <p>
 * Provides an interface to an object which can determine whether or not an {@link
 * org.semanticweb.owlapi.model.OWLObjectProperty} is satisfiable.
 * </p>
 */
public interface ObjectPropertySatisfiabilityChecker {

    /**
     * Determines whether or not the specified property is satisfiable.
     * @param property The property
     * @return <code>true</code> if the specified property is satisfiable, otherwise <code>false</code>.
     */
    boolean isSatisfiable(OWLObjectProperty property);
}
