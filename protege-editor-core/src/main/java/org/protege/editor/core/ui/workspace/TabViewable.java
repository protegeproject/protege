package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.ui.view.ViewComponentPlugin;

public interface TabViewable {
	public boolean checkViewable(WorkspaceTabPlugin plugin);

	boolean checkViewable(ViewComponentPlugin plugin);

}
