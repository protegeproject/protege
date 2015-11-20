package org.protege.editor.core.ui.view;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class DisposableAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 2598213860026917513L;


    protected DisposableAction(String name, Icon icon) {
        super(name, icon);
    }


    /**
     * This method should be overriden and used to
     * detatch listeners etc.
     */
    public abstract void dispose();
}
