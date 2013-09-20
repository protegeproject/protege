package org.protege.editor.owl.model.classexpression;

import org.semanticweb.owlapi.model.OWLClassExpression;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractQueryNode implements OWLClassExpressionNode {

    private OWLClassExpressionNode leftNode;

    private OWLClassExpressionNode rightNode;


    protected AbstractQueryNode(OWLClassExpressionNode leftNode, OWLClassExpressionNode rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }


    public OWLClassExpressionNode getLeftNode() {
        return leftNode;
    }


    public OWLClassExpressionNode getRightNode() {
        return rightNode;
    }


    public OWLClassExpression getClassExpression() {
        return null;
    }
}
