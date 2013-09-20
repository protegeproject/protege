package org.protege.editor.owl.ui.renderer.styledstring;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public class ComplexClassExpressionBracketingStrategy implements BracketingStrategy {

    private static ComplexClassExpressionBracketingStrategy instance = new ComplexClassExpressionBracketingStrategy();

    private ComplexClassExpressionBracketingStrategy() {
    }

    public static ComplexClassExpressionBracketingStrategy get() {
        return instance;
    }

    public boolean shouldBracket(OWLObject parentObject, OWLObject childObject) {
        return !(parentObject instanceof OWLAxiom) && childObject instanceof OWLClassExpression && ((OWLClassExpression) childObject).isAnonymous();
    }
}
