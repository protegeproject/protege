package org.protege.editor.core.ui.action;

import org.protege.editor.core.ProtegeManager;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class CloseAction extends ProtegeAction {

    public void actionPerformed(ActionEvent e) {
        ProtegeManager.getInstance().getEditorKitManager().getWorkspaceManager().doClose(getWorkspace());
    }


    public void initialise() throws Exception {

    }


    public void dispose() {

    }
}
