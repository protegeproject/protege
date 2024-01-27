package org.protege.editor.core.ui.tabbedpane;

import javax.swing.JTabbedPane;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/03/15
 */
public class NullTabCloseHandler implements TabCloseHandler {

    @Override
    public boolean shouldCloseTab(int tabIndex, JTabbedPane tabbedPane) {
        return false;
    }

    @Override
    public void handleCloseTab(int tabIndex, JTabbedPane tabbedPane) {

    }
}
