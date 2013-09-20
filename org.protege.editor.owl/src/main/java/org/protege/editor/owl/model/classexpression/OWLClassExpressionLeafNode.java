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
public class OWLClassExpressionLeafNode implements OWLClassExpressionNode {

    private OWLClassExpression description;


    public OWLClassExpressionLeafNode(OWLClassExpression description) {
        this.description = description;
    }


    public OWLClassExpression getClassExpression() {
        return description;
    }


    public void accept(OWLClassExpressionNodeVisitor visitor) {
        visitor.visit(this);
    }


    public OWLClassExpressionNode getLeftNode() {
        return null;
    }


    public OWLClassExpressionNode getRightNode() {
        return null;
    }


    public void setLeftNode(OWLClassExpressionNode node) {
    }


    public void setRightNode(OWLClassExpressionNode node) {
    }
}
