package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;

public interface TabViewable {
	public boolean checkViewable(WorkspaceTabPlugin plugin);

	public boolean checkViewable(ViewComponentPlugin plugin);
	
	public boolean isReadOnly(ViewComponentPlugin view);
	
	public boolean isRequired(WorkspaceTabPlugin view);

}
