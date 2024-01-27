package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.view.CreateNewChildTarget;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-May-2007<br><br>

 * Create a new object and makes it the child of the selected object
 */
public class CreateNewChildAction extends FocusedComponentAction<CreateNewChildTarget> {

    protected Class<CreateNewChildTarget> initialiseAction() {
        return CreateNewChildTarget.class;
    }


    protected boolean canPerform() {
        return getCurrentTarget().canCreateNewChild();
    }


    public void actionPerformed(ActionEvent e) {
        getCurrentTarget().createNewChild();
    }
}
