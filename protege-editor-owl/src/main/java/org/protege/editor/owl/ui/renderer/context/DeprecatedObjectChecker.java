package org.protege.editor.owl.ui.renderer.context;

import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public interface DeprecatedObjectChecker {

    /**
     * Determines whether or not the specified entity is deprecated.
     * @param entity The entity.
     * @return <code>true</code> if the entity is deprecated, otherwise <code>false</code>.
     */
    boolean isDeprecated(OWLEntity entity);
}
