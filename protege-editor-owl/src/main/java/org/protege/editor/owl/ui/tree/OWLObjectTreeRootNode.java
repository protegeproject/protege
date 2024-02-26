package org.protege.editor.owl.ui.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>

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
        List<N> sortedRoots = new ArrayList<>(this.roots);
        if (tree.getOWLObjectComparator() != null) {
            Collections.sort(sortedRoots, tree.getOWLObjectComparator());
        }
        for (N root : sortedRoots) {
            add(tree.createTreeNode(root));
        }
    }
}
