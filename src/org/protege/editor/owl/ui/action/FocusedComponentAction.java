package org.protege.editor.owl.ui.action;

import javax.swing.FocusManager;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
