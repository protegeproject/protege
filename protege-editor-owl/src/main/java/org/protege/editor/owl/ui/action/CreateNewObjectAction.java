package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.view.CreateNewTarget;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-May-2007<br><br>
 */
public class CreateNewObjectAction extends FocusedComponentAction<CreateNewTarget> {

    protected boolean canPerform() {
        return getCurrentTarget().canCreateNew();
    }


    protected Class<CreateNewTarget> initialiseAction() {
        return CreateNewTarget.class;
    }


    public void actionPerformed(ActionEvent e) {
        getCurrentTarget().createNewObject();
    }
}
