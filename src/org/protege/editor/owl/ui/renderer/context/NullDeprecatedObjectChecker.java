package org.protege.editor.owl.ui.renderer.context;

import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public class NullDeprecatedObjectChecker implements DeprecatedObjectChecker {

    public boolean isDeprecated(OWLEntity entity) {
        return false;
    }
}
