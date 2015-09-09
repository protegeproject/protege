package org.protege.editor.core.ui.tabbedpane;

import org.protege.editor.core.ui.workspace.WorkspaceTab;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/03/15
 */
public class WorkspaceTabCloseHandler implements TabCloseHandler {

    private Logger logger = Logger.getLogger(TabCloseHandler.class.getName());

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
            logger.severe("Problem disposing of tab: " + e.getMessage());
        }
    }
}
