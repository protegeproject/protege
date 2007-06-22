package org.protege.editor.owl.ui.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
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
 * Medical Informatics Group<br>
 * Date: 07-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A helper class that manages change listeners.  This is typically
 * used by views that implement Copyable, Pasteable etc.
 */
public class ChangeListenerMediator {

    private List<ChangeListener> listeners;


    public ChangeListenerMediator() {
        listeners = new ArrayList<ChangeListener>();
    }


    /**
     * Removes all change listeners
     */
    public void clearListeners() {
        listeners.clear();
    }


    /**
     * Adds a change listener that will be notified of state change
     * events.
     */
    public void addChangeListener(ChangeListener changeListener) {
        listeners.add(changeListener);
    }


    /**
     * Removes a previously added listener
     */
    public void removeChangeListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
    }


    /**
     * Notifies all listeners of a state change event.
     * @param source The cause of the state change.
     */
    public void fireStateChanged(Object source) {
        for (ChangeListener listener : new ArrayList<ChangeListener>(listeners)) {
            listener.stateChanged(new ChangeEvent(source));
        }
    }


    public void dispose() {
        listeners.clear();
    }
}
