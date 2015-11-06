package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.view.Deleteable;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-May-2007<br><br>
 */
public class DeleteAction extends FocusedComponentAction<Deleteable> {

    protected boolean canPerform() {
        return getCurrentTarget().canDelete();
    }


    public void actionPerformed(ActionEvent e) {
        Deleteable currentTarget = getCurrentTarget();
        if (currentTarget != null) {
            currentTarget.handleDelete();
        }
    }


    protected Class<Deleteable> initialiseAction() {
        return Deleteable.class;
    }
}
