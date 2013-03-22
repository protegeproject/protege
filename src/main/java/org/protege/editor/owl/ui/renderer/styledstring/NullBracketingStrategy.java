package org.protege.editor.owl.ui.renderer.styledstring;

import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public class NullBracketingStrategy implements BracketingStrategy {

    private static final NullBracketingStrategy instance = new NullBracketingStrategy();

    public static NullBracketingStrategy get() {
        return instance;
    }

    private NullBracketingStrategy() {
    }

    public boolean shouldBracket(OWLObject parentObject, OWLObject childObject) {
        return false;
    }
}
