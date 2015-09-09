package org.protege.editor.owl.ui.renderer.menu;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ui.preferences.PreferencesDialogPanel;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;

public class ConfigureRenderer extends ProtegeOWLAction {
	private static final long serialVersionUID = -6789172694929034645L;

	public void initialise() throws Exception {

	}

	public void dispose() throws Exception {

	}

	public void actionPerformed(ActionEvent e) {
        PreferencesDialogPanel.showPreferencesDialog("Renderer",  getOWLEditorKit());
	}

}
