package org.protege.editor.core.ui.tabbedpane;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.protege.editor.core.ui.view.View;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/03/15
 */
public class ViewTabCloseHandler implements TabCloseHandler {

    @Override
    public void handleCloseTab(int tabIndex, JTabbedPane tabbedPane) {
        View view = (View) SwingUtilities.getAncestorOfClass(View.class, tabbedPane);
        if(view != null) {
            view.closeView();
        }
    }

    @Override
    public boolean shouldCloseTab(int tabIndex, JTabbedPane tabbedPane) {
        return true;
    }
}
