package org.protege.editor.core.ui.preferences.node;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractPreferenceNode<V extends Object> implements PreferenceNode<V> {

    private String label;


    public AbstractPreferenceNode(String label) {
        this.label = label;
    }


    public String getLabel() {
        return label;
    }
}
