package org.protege.editor.owl.ui.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;


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
        listeners = new ArrayList<>();
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
        for (ChangeListener listener : new ArrayList<>(listeners)) {
            listener.stateChanged(new ChangeEvent(source));
        }
    }


    public void dispose() {
        listeners.clear();
    }
}
