package org.protege.editor.core.ui.action;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.error.ErrorLogPanel;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 26-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SaveAction extends ProtegeAction {
    private static final long serialVersionUID = 3838354503751266687L;

    public void actionPerformed(ActionEvent e) {
        try {
            ProtegeManager.getInstance().saveEditorKit(getEditorKit());
        }
        catch (Exception e1) {
            ErrorLogPanel.showErrorDialog(e1);
        }
    }


    public void dispose() {
    }


    public void initialise() throws Exception {

    }
}
