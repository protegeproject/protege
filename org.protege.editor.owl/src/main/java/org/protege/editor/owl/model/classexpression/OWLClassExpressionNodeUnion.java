package org.protege.editor.owl.model.classexpression;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassExpressionNodeUnion extends AbstractQueryNode {

    public OWLClassExpressionNodeUnion(OWLClassExpressionNode leftNode, OWLClassExpressionNode rightNode) {
        super(leftNode, rightNode);
    }


    public void accept(OWLClassExpressionNodeVisitor visitor) {
        visitor.visit(this);
    }
}
