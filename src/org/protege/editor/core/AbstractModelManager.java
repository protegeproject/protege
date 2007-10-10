package org.protege.editor.core;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An abstract class that implements enough functionality from
 * <code>ModelManager</code> to support adding, removing of
 * <code>ModelManagerListener</code>s and firing of
 * <code>ModelManager</code> events.
 */
public abstract class AbstractModelManager implements ModelManager {


    private List<ModelManagerListener> listeners;


    protected AbstractModelManager() {
        listeners = new ArrayList<ModelManagerListener>();
    }


    public void addModelManagerListener(ModelManagerListener listener) {
        listeners.add(listener);
    }


    public void removeModelManagerListener(ModelManagerListener listener) {
        listeners.remove(listener);
    }


    public void fireModelManagerEvent(ModelManagerEvent event) {
        for (ModelManagerListener listener : new ArrayList<ModelManagerListener>(listeners)) {
            listener.handleEvent(event);
        }
    }
}
