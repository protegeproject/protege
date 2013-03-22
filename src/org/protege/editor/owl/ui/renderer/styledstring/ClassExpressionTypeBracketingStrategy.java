package org.protege.editor.owl.ui.renderer.styledstring;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/10/2012
 */
public class ClassExpressionTypeBracketingStrategy implements BracketingStrategy {


    private ClassExpressionType classExpressionType;

    private ClassExpressionTypeBracketingStrategy(ClassExpressionType classExpressionType) {
        this.classExpressionType = classExpressionType;
    }

    public static ClassExpressionTypeBracketingStrategy get(ClassExpressionType classExpressionType) {
        return new ClassExpressionTypeBracketingStrategy(classExpressionType);
    }

    public boolean shouldBracket(OWLObject parentObject, OWLObject childObject) {
        return childObject instanceof OWLClassExpression && ((OWLClassExpression) childObject).getClassExpressionType().equals(classExpressionType);
    }
}
