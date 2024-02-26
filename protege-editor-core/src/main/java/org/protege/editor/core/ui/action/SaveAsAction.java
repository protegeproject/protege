package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.error.ErrorLogPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SaveAsAction extends ProtegeAction {



    public void actionPerformed(ActionEvent e) {
        try {
            ProtegeManager.getInstance().saveEditorKitAs(getEditorKit());
        }
        catch (Exception ex) {
            ErrorLogPanel.showErrorDialog(ex);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
