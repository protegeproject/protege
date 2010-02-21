package org.protege.editor.core.ui.preferences;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ui.action.ProtegeAction;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ShowPreferencesAction extends ProtegeAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8689829614868877045L;


    public void actionPerformed(ActionEvent e) {
        PreferencesDialogPanel.showPreferencesDialog(null, getEditorKit());
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
