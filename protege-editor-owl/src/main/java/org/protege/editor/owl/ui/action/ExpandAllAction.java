package org.protege.editor.owl.ui.action;


import org.protege.editor.owl.ui.view.HasExpandAll;

import java.awt.event.ActionEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 31/08/15
 */
public class ExpandAllAction extends FocusedComponentAction<HasExpandAll> {

    @Override
    protected Class<HasExpandAll> initialiseAction() {
        return HasExpandAll.class;
    }

    @Override
    protected boolean canPerform() {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        HasExpandAll hasExpandAll = getCurrentTarget();
        hasExpandAll.expandAll();
    }
}
