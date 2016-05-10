package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 25-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLSelectionViewAction extends DisposableAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8530394736100720111L;


    protected OWLSelectionViewAction(String name, Icon icon) {
        super(name, icon);
    }


    public abstract void updateState();
}
