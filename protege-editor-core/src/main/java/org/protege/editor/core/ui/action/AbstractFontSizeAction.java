package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractFontSizeAction extends ProtegeAction {

    /**
     * 
     */
    private static final long serialVersionUID = -2557130094572814442L;


    public void actionPerformed(ActionEvent e) {
        getWorkspace().changeFontSize(getDelta());
    }


    public void initialise() throws Exception {

    }


    public void dispose() {
    }


    protected abstract int getDelta();
}
