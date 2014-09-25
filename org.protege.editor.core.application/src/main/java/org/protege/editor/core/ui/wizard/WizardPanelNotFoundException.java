package org.protege.editor.core.ui.wizard;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


public class WizardPanelNotFoundException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -967700880863172861L;
    private Object id;

    public WizardPanelNotFoundException(Object id) {
        super();
        this.id = id;
    }


    public String getMessage() {
        return id.toString();
    }
}
