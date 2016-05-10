package org.protege.editor.owl.model.hierarchy.tabbed;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class Edge {

    private String child;

    private String parent;


    public Edge(String child, String parent) {
        this.child = child;
        this.parent = parent;
    }


    public String getChild() {
        return child;
    }


    public String getParent() {
        return parent;
    }


    public boolean isRoot() {
        return parent == null;
    }


    public String toString() {
        return child + " --> " + parent;
    }
}
