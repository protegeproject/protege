package org.protege.editor.owl.ui.tree;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLObject;


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

    public OWLObjectTreeNode(Object userObject, OWLObjectTree tree) {
        super(userObject);
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
        int count = super.getChildCount();
        return count;
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
