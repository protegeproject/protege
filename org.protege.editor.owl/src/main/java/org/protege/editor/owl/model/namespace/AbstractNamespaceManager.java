package org.protege.editor.owl.model.namespace;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractNamespaceManager implements NamespaceManager {

    private List<NamespaceManagerListener> listeners;


    protected AbstractNamespaceManager() {
        listeners = new ArrayList<NamespaceManagerListener>();
    }


    public void addListener(NamespaceManagerListener listener) {
        listeners.add(listener);
    }


    public void removeListener(NamespaceManagerListener listener) {
        listeners.remove(listener);
    }


    protected void fireMappingAdded(String prefix, String namespace) {
        for (NamespaceManagerListener listener : new ArrayList<NamespaceManagerListener>(listeners)) {
            listener.mappingAdded(prefix, namespace);
        }
    }


    protected void fireMappingRemoved(String prefix, String namespace) {
        for (NamespaceManagerListener listener : new ArrayList<NamespaceManagerListener>(listeners)) {
            listener.mappingRemoved(prefix, namespace);
        }
    }
}
