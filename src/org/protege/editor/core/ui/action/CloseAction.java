package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.protege.editor.core.ProtegeManager;


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
        int ret = JOptionPane.showConfirmDialog(ProtegeManager.getInstance().getFrame(getWorkspace()),
                                                "Close the current set of ontologies?",
                                                "Close?",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE);
        if (ret == JOptionPane.YES_OPTION) {
            ProtegeManager.getInstance().getEditorKitManager().removeEditorKit(getEditorKit());
        }
    }


    public void initialise() throws Exception {

    }


    public void dispose() {

    }
}
