package org.protege.editor.owl.model.classexpression;

import org.semanticweb.owlapi.model.OWLClassExpression;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Oct-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLClassExpressionNode {

    OWLClassExpression getClassExpression();


    void accept(OWLClassExpressionNodeVisitor visitor);


    OWLClassExpressionNode getLeftNode();


    OWLClassExpressionNode getRightNode();
}
