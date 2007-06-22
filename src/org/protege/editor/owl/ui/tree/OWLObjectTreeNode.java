package org.protege.editor.owl.ui.tree;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;
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
public class OWLObjectTreeNode<N extends OWLObject> extends DefaultMutableTreeNode {

    private static final Logger logger = Logger.getLogger(OWLObjectTreeNode.class);

    private OWLObjectTree tree;

    private boolean isLoaded;

    private Set<N> equivalentObjects;

    // private static int count = 0;


    public OWLObjectTreeNode(Object userObject, OWLObjectTree tree) {
        super(userObject);
        //  count++;
        //System.out.println("Created: [" + count + "] " + userObject);
        this.tree = tree;
        isLoaded = false;
        equivalentObjects = new HashSet<N>();
    }


    public void addEquivalentObject(N object) {
        equivalentObjects.add(object);
    }


    public Set<N> getEquivalentObjects() {
        if (getUserObject() == null) {
            return Collections.emptySet();
        }
        Set<N> equivalents = tree.getProvider().getEquivalents((OWLObject) getUserObject());
        equivalents.remove(getUserObject());
        return equivalents;
    }


    public OWLObjectTreeNode(OWLObjectTree tree) {
        this.tree = tree;
        this.equivalentObjects = new HashSet<N>();
    }


    public boolean isRoot() {
        return getUserObject() == null;
    }


    public N getOWLObject() {
        return (N) getUserObject();
    }


    protected boolean isLoaded() {
        return isLoaded;
    }


    protected void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }


    protected synchronized void loadChildrenIfNecessary() {
        if (isLoaded) {
            return;
        }
        isLoaded = true;
        OWLObject parentObject = null;
        OWLObjectTreeNode<N> parentNode = (OWLObjectTreeNode) getParent();
        if (getParent() != null) {
            parentObject = parentNode.getOWLObject();
        }
        List<OWLObjectTreeNode<N>> nodes = tree.getChildNodes(this);
        for (OWLObjectTreeNode<N> child : nodes) {
//                if (parentObject != null && parentObject.equals(child.getOWLObject())) {
//                    // Cycle!!
//                } else {
            add(child);
//                }
        }
    }


    public TreeNode getChildAt(int childIndex) {
        loadChildrenIfNecessary();
        return super.getChildAt(childIndex);
    }


    public int getChildCount() {
        loadChildrenIfNecessary();
        return super.getChildCount();
    }


    public TreeNode getParent() {
        return super.getParent();
    }


    public int getIndex(TreeNode node) {
        loadChildrenIfNecessary();
        return super.getIndex(node);
    }


    public boolean getAllowsChildren() {
        return true;
    }


    public boolean isLeaf() {
        return getChildCount() == 0;
    }


    public Enumeration children() {
        loadChildrenIfNecessary();
        return super.children();
    }
}
