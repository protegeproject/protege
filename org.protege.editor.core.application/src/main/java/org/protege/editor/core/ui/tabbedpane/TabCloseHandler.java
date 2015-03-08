package org.protege.editor.core.ui.tabbedpane;

import javax.swing.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/03/15
 */
public interface TabCloseHandler {

    boolean shouldCloseTab(int tabIndex, JTabbedPane tabbedPane);

    void handleCloseTab(int tabIndex, JTabbedPane tabbedPane);
}
