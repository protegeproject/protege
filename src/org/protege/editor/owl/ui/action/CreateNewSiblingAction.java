package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.view.CreateNewSiblingTarget;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-May-2007<br><br>
 */
public class CreateNewSiblingAction extends FocusedComponentAction<CreateNewSiblingTarget> {

    protected Class<CreateNewSiblingTarget> initialiseAction() {
        return CreateNewSiblingTarget.class;
    }


    protected boolean canPerform() {
        return getCurrentTarget().canCreateNewSibling();
    }


    public void actionPerformed(ActionEvent e) {
        getCurrentTarget().createNewSibling();
    }
}
