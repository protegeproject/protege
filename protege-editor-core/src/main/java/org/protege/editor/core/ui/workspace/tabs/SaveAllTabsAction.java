package org.protege.editor.core.ui.workspace.tabs;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.slf4j.LoggerFactory;

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
			LoggerFactory.getLogger(SaveAllTabsAction.class)
					.error("An error occurred when saving the state of the workspace: {}", e);
		}
	}

}
