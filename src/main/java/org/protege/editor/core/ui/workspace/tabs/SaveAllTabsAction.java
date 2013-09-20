package org.protege.editor.core.ui.workspace.tabs;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.action.ProtegeAction;

public class SaveAllTabsAction extends ProtegeAction {
	private static final long serialVersionUID = 5435208919435226000L;

	public SaveAllTabsAction() {
	}

	public void initialise() throws Exception {
	}


	public void dispose() throws Exception {
	}


	public void actionPerformed(ActionEvent event) {
		try {
			getWorkspace().save();
		}
		catch (Exception e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
	}

}
