package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.view.HasDisplayDeprecatedEntities;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16/11/15
 */
public class DisplayDeprecatedEntitiesAction extends ComponentHierarchyAction<HasDisplayDeprecatedEntities> {

    @Override
    protected Class<HasDisplayDeprecatedEntities> initialiseAction() {
        putValue(SELECTED_KEY, false);
        return HasDisplayDeprecatedEntities.class;
    }

    @Override
    protected void actionPerformedOnTarget(ActionEvent e, HasDisplayDeprecatedEntities target) {
        boolean value = (boolean) getValue(SELECTED_KEY);
        target.setShowDeprecatedEntities(value);
    }
}
