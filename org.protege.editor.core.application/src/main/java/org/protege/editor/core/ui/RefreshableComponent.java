package org.protege.editor.core.ui;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Nov-2007<br><br>
 *
 * A marker interface for components that can regenerate
 * the view that they display.
 */
public interface RefreshableComponent {

    /**
     * Clears the data displayed by the component and
     * reloads data.
     */
    void refreshComponent();
}
