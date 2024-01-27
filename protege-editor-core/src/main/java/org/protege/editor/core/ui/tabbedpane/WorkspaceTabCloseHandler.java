package org.protege.editor.core.ui.tabbedpane;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/03/15
 */
public class WorkspaceTabCloseHandler implements TabCloseHandler {

    private final Logger logger = LoggerFactory.getLogger(TabCloseHandler.class.getName());

    @Override
    public boolean shouldCloseTab(int tabIndex, JTabbedPane tabbedPane) {
        int ret = JOptionPane.showConfirmDialog(tabbedPane,
                String.format("Do you want to close the %s tab?", tabbedPane.getTitleAt(tabIndex)),
                "Close tab?",
                JOptionPane.YES_NO_OPTION);
        return ret == JOptionPane.YES_OPTION;
    }

    @Override
    public void handleCloseTab(int tabIndex, JTabbedPane tabbedPane) {
        WorkspaceTab workspaceTab = (WorkspaceTab) tabbedPane.getComponentAt(tabIndex);
        workspaceTab.getWorkspace().removeTab(workspaceTab);
        try {
            workspaceTab.dispose();
        } catch (Exception e) {
            logger.error("An error occurred whilst disposing of a tab.", e);
        }
    }
}
