package org.protege.editor.core.ui.util;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 21, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractSelectionProvider implements SelectionProvider {

    private List<SelectionProviderListener> listeners;


    public AbstractSelectionProvider() {
        listeners = new ArrayList<SelectionProviderListener>();
    }


    // Add listener
    public void addSelectionProviderListener(SelectionProviderListener listener) {
        listeners.add(listener);
    }


    // Remove listener
    public void removeSelectionProviderListener(SelectionProviderListener listener) {
        listeners.remove(listener);
    }


    protected void fireSelectionChanged() {
        for (SelectionProviderListener listener : new ArrayList<SelectionProviderListener>(listeners)) {
            listener.selectionChanged(this);
        }
    }
}
