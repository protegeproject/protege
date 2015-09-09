package org.protege.editor.core.ui.workspace.tabs;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;

public class ImportTabAction extends ProtegeAction {
	private static final long serialVersionUID = 7952432018266375998L;
	public static final Logger LOGGER = Logger.getLogger(ImportTabAction.class);

	public ImportTabAction() {
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
            File f = UIUtil.openFile((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, workspace),
                                     "Save layout to",
                                     "XML Layout File",
                                     extensions);
            if (f == null) {
                return;
            }
            BufferedReader reader = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            WorkspaceViewsTab tab = (WorkspaceViewsTab) CreateTabAction.handleCreateNewTab(workspace);
            if (tab == null) {
                return;
            }
            tab.reset(sb.toString());
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
