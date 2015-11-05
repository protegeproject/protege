package org.protege.editor.core.log;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.action.ProtegeAction;

import java.awt.event.ActionEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class ShowLogAction extends ProtegeAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        ProtegeApplication.showLogView();
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }
}
