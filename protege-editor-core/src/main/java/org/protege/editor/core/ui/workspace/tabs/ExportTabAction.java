package org.protege.editor.core.ui.workspace.tabs;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ExportTabAction extends ProtegeAction {

	private static final long serialVersionUID = 7371237404306047078L;

	private final Logger logger = LoggerFactory.getLogger(ExportTabAction.class);

	public ExportTabAction() {
	}

	public void initialise() throws Exception {
	}

	public void dispose() throws Exception {
	}

	public void actionPerformed(ActionEvent event) {
		TabbedWorkspace workspace = (TabbedWorkspace) getWorkspace();
		Set<String> extensions = new HashSet<>();
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
		try {
			f.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(f);
			((WorkspaceViewsTab) workspace.getSelectedTab()).getViewsPane().saveViews(writer);
			writer.close();
			JOptionPane.showMessageDialog(workspace, "Layout saved to: " + f);
		}
		catch (IOException e) {
			logger.error("An error occurred when saving a tab layout to {}.", f, e);
			JOptionPane.showMessageDialog(workspace,
					"There was a problem saving the layout",
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
