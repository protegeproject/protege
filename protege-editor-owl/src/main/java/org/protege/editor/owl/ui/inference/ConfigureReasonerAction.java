package org.protege.editor.owl.ui.inference;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ui.preferences.PreferencesDialogPanel;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

public class ConfigureReasonerAction extends ProtegeOWLAction {
    private static final long serialVersionUID = -5290349827430843731L;

    public void initialise() throws Exception {
        
    }

    public void dispose() throws Exception {

    }

    public void actionPerformed(ActionEvent e) {
        PreferencesDialogPanel.showPreferencesDialog("Reasoner",  getOWLEditorKit());
    }

}
