package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 01/04/15
 */
public class FindAction extends ProtegeOWLAction {

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getOWLWorkspace().showSearchDialog();
    }
}
