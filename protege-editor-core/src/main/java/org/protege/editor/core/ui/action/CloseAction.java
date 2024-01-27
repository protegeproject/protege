package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ProtegeManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jul-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class CloseAction extends ProtegeAction {

    /**
     * 
     */
    private static final long serialVersionUID = -4440291563885619743L;


    public void actionPerformed(ActionEvent e) {
        ProtegeManager.getInstance().getEditorKitManager().getWorkspaceManager().doClose(getWorkspace());
    }


    public void initialise() throws Exception {

    }


    public void dispose() {

    }
}
