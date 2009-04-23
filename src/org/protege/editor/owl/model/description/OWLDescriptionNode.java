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
public interface OWLDescriptionNode {

    OWLClassExpression getClassExpression();


    void accept(OWLDescriptionNodeVisitor visitor);


    OWLDescriptionNode getLeftNode();


    OWLDescriptionNode getRightNode();
}
