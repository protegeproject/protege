package org.protege.editor.owl.ui.renderer.styledstring;

import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public interface BracketingStrategy {

    boolean shouldBracket(OWLObject parentObject, OWLObject childObject);
}
