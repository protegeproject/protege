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
public interface OWLClassExpressionNodeVisitor {

    public void visit(OWLClassExpressionNodeDifference node);


    public void visit(OWLClassExpressionNodeUnion node);


    public void visit(OWLClassExpressionLeafNode node);


    public void visit(OWLClassExpressionNodePossibly node);
}
