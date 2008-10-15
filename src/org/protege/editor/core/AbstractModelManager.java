package org.protege.editor.core;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    private static final Logger logger = Logger.getLogger(AbstractModelManager.class);

    private List<ModelManagerListener> listeners;

    private Map<Object, Disposable> objects = new HashMap<Object, Disposable>();


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


    public <T extends Disposable> void put(Object key, T object) {
        objects.put(key, object);
    }


    public <T extends Disposable> T get(Object key) {
        return (T) objects.get(key);
    }


    public void dispose() {
        for (Disposable object : objects.values()){
            try {
                object.dispose();
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
        objects.clear();
    }
}
