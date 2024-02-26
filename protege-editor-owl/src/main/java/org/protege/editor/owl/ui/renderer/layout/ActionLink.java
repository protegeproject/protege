package org.protege.editor.owl.ui.renderer.layout;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.Action;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/11/2011
 * <p>Represents a link that has a Swing Action associated with it (rather than being an http link)</p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class ActionLink extends Link {

    private Action action;

    public ActionLink(Action action) {
        super(action.getValue(Action.NAME).toString());
        this.action = action;
    }

    @Override
    public void activate(Component component, MouseEvent event) {
        action.actionPerformed(new ActionEvent(component, 0, getText()));
    }

    @Override
    public boolean isRollOverLink() {
        return false;
    }
}
