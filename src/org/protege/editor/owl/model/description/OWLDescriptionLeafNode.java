package org.protege.editor.owl.model.description;

import org.semanticweb.owl.model.OWLDescription;


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

    private OWLDescription description;


    public OWLDescriptionLeafNode(OWLDescription description) {
        this.description = description;
    }


    public OWLDescription getDescription() {
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
