package org.protege.editor.core.ui.workspace.tabs;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;

public class ExportTabAction extends ProtegeAction {
	private static final long serialVersionUID = 7371237404306047078L;
	public static final Logger LOGGER = Logger.getLogger(ExportTabAction.class);

	public ExportTabAction() {
	}

	public void initialise() throws Exception {
	}

	public void dispose() throws Exception {
	}

	public void actionPerformed(ActionEvent event) {
		TabbedWorkspace workspace = (TabbedWorkspace) getWorkspace();
		try {
			Set<String> extensions = new HashSet<String>();
			extensions.add("xml");
			String fileName = workspace.getSelectedTab().getLabel().replace(' ', '_') + ".layout.xml";
			File f = UIUtil.saveFile((Window) SwingUtilities.getAncestorOfClass(Window.class, workspace),
					"Save layout to",
					"XML Layout",
					extensions,
					fileName);
			if (f == null) {
				return;
			}
			f.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(f);
			((WorkspaceViewsTab) workspace.getSelectedTab()).getViewsPane().saveViews(writer);
			writer.close();
			JOptionPane.showMessageDialog(workspace, "Layout saved to: " + f);
		}
		catch (IOException e) {
			LOGGER.error(e);
			JOptionPane.showMessageDialog(workspace,
					"There was a problem saving the layout",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
