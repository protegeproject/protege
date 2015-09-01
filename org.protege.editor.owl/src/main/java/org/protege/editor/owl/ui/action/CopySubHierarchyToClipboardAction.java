package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.view.HasCopySubHierarchyToClipboard;

import java.awt.event.ActionEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/08/15
 */
public class CopySubHierarchyToClipboardAction extends FocusedComponentAction<HasCopySubHierarchyToClipboard> {

    @Override
    protected Class<HasCopySubHierarchyToClipboard> initialiseAction() {
        return HasCopySubHierarchyToClipboard.class;
    }

    @Override
    protected boolean canPerform() {
        return getCurrentTarget().canPerformCopySubHierarchyToClipboard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getCurrentTarget().copySubHierarchyToClipboard();
    }
}
