package org.protege.editor.owl.ui.action;

import javax.swing.FocusManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-May-2007<br><br>
 */
public abstract class FocusedComponentAction<C extends ActionTarget> extends ProtegeOWLAction {

    private Class<C> targetClass;

    private C currentTarget;

    private PropertyChangeListener listener;

    private ChangeListener changeListener;


    final public void initialise() throws Exception {
        FocusManager.getCurrentManager().addPropertyChangeListener(listener = evt -> {
            if (evt.getPropertyName().equals("focusOwner")) {
                update();
            }
        });
        changeListener = e -> update();
        targetClass = initialiseAction();
        update();
    }


    protected abstract Class<C> initialiseAction();

    protected void targetChanged() {

    }

    private void update() {
        Component c = FocusManager.getCurrentManager().getFocusOwner();
        if (c instanceof JRootPane || c == null) {
            return;
        }
        // By default text components are pasteable
        if (c instanceof TextComponent) {
            setEnabled(true);
            return;
        }

        C target = getTarget();
        if (currentTarget != null) {
            detatchListener();
        }
        currentTarget = target;
        if (currentTarget != null) {
            attatchListeners();
        }
        setEnabled(currentTarget != null && canPerform());
        targetChanged();
    }


    protected abstract boolean canPerform();


    public C getCurrentTarget() {
        return currentTarget;
    }


    private C getTarget() {
        Component c = FocusManager.getCurrentManager().getFocusOwner();
        if (targetClass.isInstance(c)) {
            return (C) c;
        }
        return (C) SwingUtilities.getAncestorOfClass(targetClass, c);
    }


    private void attatchListeners() {
        currentTarget.addChangeListener(changeListener);
    }


    private void detatchListener() {
        currentTarget.removeChangeListener(changeListener);
    }


    public void dispose() {
        FocusManager.getCurrentManager().removePropertyChangeListener(listener);
    }
}
