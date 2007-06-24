package org.protege.editor.owl.ui.action;

import java.awt.Component;
import java.awt.TextComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.FocusManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


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
        FocusManager.getCurrentManager().addPropertyChangeListener(listener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("focusOwner")) {
                    update();
                }
            }
        });
        changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                update();
            }
        };
        targetClass = initialiseAction();
        update();
    }


    protected abstract Class<C> initialiseAction();


    private void update() {
        Component c = FocusManager.getCurrentManager().getFocusOwner();
        // By default text components are pasteable
        if (c instanceof TextComponent) {
            setEnabled(true);
            return;
        }

        C target = getPasteable();
        if (currentTarget != null) {
            detatchListener();
        }
        currentTarget = target;
        if (currentTarget != null) {
            attatchListeners();
        }
        setEnabled(currentTarget != null && canPerform());
    }


    protected abstract boolean canPerform();


    public C getCurrentTarget() {
        return currentTarget;
    }


    private C getPasteable() {
        Component c = FocusManager.getCurrentManager().getFocusOwner();
        if (targetClass.isInstance(c)) {
            return (C) c;
        }
        if (c == null) {
            return null;
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
