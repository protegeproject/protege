package org.protege.editor.owl.ui.breadcrumb;

import org.protege.editor.owl.ui.action.ProtegeOWLRadioButtonAction;

import java.awt.event.ActionEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 22 Aug 2017
 */
public class ShowBreadcrumbTrailAction extends ProtegeOWLRadioButtonAction {

    @Override
    protected void update() {
        setSelected(getOWLWorkspace().isBreadcrumbTrailVisible());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getOWLWorkspace().setBreadcrumbTrailVisible(isSelected());
    }

    @Override
    public void dispose() throws Exception {

    }

    @Override
    public void initialise() throws Exception {
        update();
    }
}
