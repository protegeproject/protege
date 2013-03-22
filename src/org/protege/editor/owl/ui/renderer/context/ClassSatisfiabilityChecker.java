package org.protege.editor.owl.ui.renderer.context;

import org.semanticweb.owlapi.model.OWLClass;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 * <p>
 * Provides an interface to an object which can determine whether or not an {@link OWLClass} is satisfiable.
 * </p>
 */
public interface ClassSatisfiabilityChecker {

    /**
     * Dtermines whether or not the specified class is satisifiable.
     * @param cls The class
     * @return <code>true</code> if the specified class is satisfiable, otherwise <code>false</code>.
     */
    boolean isSatisfiable(OWLClass cls);

}
