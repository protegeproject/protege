package org.protege.editor.owl.ui.tree;

import org.semanticweb.owl.model.OWLObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectTreeRootNode<N extends OWLObject> extends OWLObjectTreeNode<N> {

    public static final Object ROOT_OBJECT = "ROOT";

//    private OWLObjectHierarchyProvider<N> provider;

    private OWLObjectTree<N> tree;

    private Set<N> roots;

//    public OWLObjectTreeRootNode(OWLObjectTree<N> tree, OWLObjectHierarchyProvider<N> provider) {
//        this(tree, provider.getRoots());
//    }


    public OWLObjectTreeRootNode(OWLObjectTree<N> tree, Set<N> roots) {
        super(tree);
        this.tree = tree;
        this.roots = roots;
    }


    protected void loadChildrenIfNecessary() {
        if (isLoaded()) {
            return;
        }
        setLoaded(true);
        List<N> sortedRoots = new ArrayList<N>(this.roots);
        if (tree.getOWLObjectComparator() != null) {
            Collections.sort(sortedRoots, tree.getOWLObjectComparator());
        }
        for (N root : sortedRoots) {
            add(tree.createTreeNode(root));
        }
    }
}
