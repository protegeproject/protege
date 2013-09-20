package org.protege.editor.owl.ui.renderer.context;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public class NullClassSatisfiabilityChecker implements ClassSatisfiabilityChecker {

    public static final boolean DEFAULT_SATISFIABLE_VALUE = true;

    public boolean isSatisfiable(OWLClass cls) {
        return DEFAULT_SATISFIABLE_VALUE;
    }

    public boolean isSatisfiable(OWLObjectProperty property) {
        return DEFAULT_SATISFIABLE_VALUE;
    }

    public boolean isSatisfiable(OWLDataProperty property) {
        return DEFAULT_SATISFIABLE_VALUE;
    }
}
