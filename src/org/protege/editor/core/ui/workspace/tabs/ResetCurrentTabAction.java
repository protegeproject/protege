package org.protege.editor.core.ui.workspace.tabs;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.Resettable;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTab;

public class ResetCurrentTabAction extends ProtegeAction {
	private static final long serialVersionUID = 6192614623601181816L;


	public ResetCurrentTabAction() {
	}


	public void initialise() throws Exception {
	}


	public void dispose() throws Exception {
	}


	public void actionPerformed(ActionEvent e) {
        WorkspaceTab tab = ((TabbedWorkspace) getWorkspace()).getSelectedTab();
        ((Resettable) tab).reset();
        tab.validate();
	}

}
