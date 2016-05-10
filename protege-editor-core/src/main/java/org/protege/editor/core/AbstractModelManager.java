package org.protege.editor.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 17, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * An abstract class that implements enough functionality from
 * <code>ModelManager</code> to support adding, removing of
 * <code>ModelManagerListener</code>s and firing of
 * <code>ModelManager</code> events.
 */
public abstract class AbstractModelManager implements ModelManager {

    private final Logger logger = LoggerFactory.getLogger(AbstractModelManager.class);

    private Map<Object, Disposable> objects = new HashMap<>();


    protected AbstractModelManager() {
    }


    public <T extends Disposable> void put(Object key, T object) {
        objects.put(key, object);
    }


    @SuppressWarnings("unchecked")
    public <T extends Disposable> T get(Object key) {
        return (T) objects.get(key);
    }


    public void dispose() {
        for (Disposable object : objects.values()){
            try {
                object.dispose();
            }
            catch (Exception e) {
                logger.error("An error occurred whilst disposing of a model manager object.  Object: {}", object, e);
            }
        }
        objects.clear();
    }
}
