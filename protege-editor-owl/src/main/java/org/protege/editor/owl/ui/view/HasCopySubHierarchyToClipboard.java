package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.action.ActionTarget;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/08/15
 */
public interface HasCopySubHierarchyToClipboard extends ActionTarget {

    void copySubHierarchyToClipboard();

    boolean canPerformCopySubHierarchyToClipboard();
}
