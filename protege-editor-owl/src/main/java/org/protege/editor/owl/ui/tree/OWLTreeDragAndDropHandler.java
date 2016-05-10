package org.protege.editor.owl.ui.tree;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 04-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLTreeDragAndDropHandler<N> {


    public boolean canDrop(Object child, Object parent);


    public void move(N child, N fromParent, N toParent);


    public void add(N child, N parent);
}
