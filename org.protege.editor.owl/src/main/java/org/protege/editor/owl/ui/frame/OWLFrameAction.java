package org.protege.editor.owl.ui.frame;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Feb-2007<br><br>
 */
public interface OWLFrameAction {

    /**
     * Gets the name of the action.  This is typically
     * used as a label for the action in a user interface.
     */
    String getName();


    /**
     * Gets the path of the action.
     * This is of the form X/G-S, where
     * X is the parent, G is the group within
     * the parent, S is the slot within the group.
     */
    String getPath();


    void actionPerformed();
}
