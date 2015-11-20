package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.view.CreateNewChildTarget;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-May-2007<br><br>
 * <p/>
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
