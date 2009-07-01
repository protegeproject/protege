package org.protege.editor.owl.model.description;

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
public abstract class AbstractQueryNode implements OWLDescriptionNode {

    private OWLDescriptionNode leftNode;

    private OWLDescriptionNode rightNode;


    protected AbstractQueryNode(OWLDescriptionNode leftNode, OWLDescriptionNode rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
    }


    public OWLDescriptionNode getLeftNode() {
        return leftNode;
    }


    public OWLDescriptionNode getRightNode() {
        return rightNode;
    }


    public OWLClassExpression getClassExpression() {
        return null;
    }
}
