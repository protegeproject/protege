package org.protege.editor.core.ui.action;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class IncreaseFontSizeAction extends AbstractFontSizeAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8515128029951047595L;

    protected int getDelta() {
        return 1;
    }
}
