package org.protege.editor.owl.model.description;

import org.semanticweb.owl.model.OWLClassExpression;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLDescriptionLeafNode implements OWLDescriptionNode {

    private OWLClassExpression description;


    public OWLDescriptionLeafNode(OWLClassExpression description) {
        this.description = description;
    }


    public OWLClassExpression getClassExpression() {
        return description;
    }


    public void accept(OWLDescriptionNodeVisitor visitor) {
        visitor.visit(this);
    }


    public OWLDescriptionNode getLeftNode() {
        return null;
    }


    public OWLDescriptionNode getRightNode() {
        return null;
    }


    public void setLeftNode(OWLDescriptionNode node) {
    }


    public void setRightNode(OWLDescriptionNode node) {
    }
}
