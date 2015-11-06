package org.protege.editor.owl.model.selection.axioms;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 30, 2008<br><br>
 */
public abstract class AbstractAxiomSelectionStrategy implements AxiomSelectionStrategy {


    private List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    public static final String ONTOLOGIES_CHANGED = "change.ontologies";

    protected void notifyPropertyChange(String property) {
        for (PropertyChangeListener l : listeners){
        l.propertyChange(new PropertyChangeEvent(this, property, null, null));
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        listeners.add(l);
    }


    public void removePropertyChangeListener(PropertyChangeListener l) {
        listeners.remove(l);
    }
}
