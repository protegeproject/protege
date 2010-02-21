package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.error.ErrorLogPanel;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SaveAsAction extends ProtegeAction {

    /**
     * 
     */
    private static final long serialVersionUID = 8969543617298643589L;


    public void actionPerformed(ActionEvent e) {
        try {
            ProtegeManager.getInstance().saveEditorKitAs(getEditorKit());
        }
        catch (Exception e1) {

            ErrorLogPanel.showErrorDialog(e1);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
