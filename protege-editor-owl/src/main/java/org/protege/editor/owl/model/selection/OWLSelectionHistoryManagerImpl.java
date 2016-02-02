package org.protege.editor.owl.model.selection;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 07-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLSelectionHistoryManagerImpl implements OWLSelectionHistoryManager {

    private OWLSelectionModel selectionModel;

    private OWLSelectionModelListener listener;

    private List<ChangeListener> changeListeners;

    private boolean initiatedSelection;

    private Stack<OWLEntity> prevSelections;

    private Stack<OWLEntity> forwardSelections;


    private OWLEntity curSel;


    public OWLSelectionHistoryManagerImpl(OWLSelectionModel owlSelectionModel) {
        this.selectionModel = owlSelectionModel;
        changeListeners = new ArrayList<ChangeListener>();

        prevSelections = new Stack<OWLEntity>();
        forwardSelections = new Stack<OWLEntity>();

        listener = () -> handleSelection();
        selectionModel.addListener(listener);
    }


    private void handleSelection() {
        OWLObject obj = selectionModel.getSelectedObject();
        if (obj instanceof OWLEntity) {
            if (!initiatedSelection) {
                // Record selection
                if (curSel != null) {
                    prevSelections.push(curSel);
                }
                forwardSelections.clear();
            }
            curSel = (OWLEntity)obj;
            fireStateChanged();
        }
    }


    public void dispose() {
        if (listener != null) {
            selectionModel.removeListener(listener);
        }
    }


    public boolean canGoBack() {
        return !prevSelections.isEmpty();
    }


    public void goBack() {
        if (!canGoBack()) {
            return;
        }
        initiatedSelection = true;
        // Pop of prevsel stack
        OWLEntity entity = prevSelections.pop();
        // Push on to forward stack
        forwardSelections.push(curSel);
        selectionModel.setSelectedEntity(entity);
        initiatedSelection = false;
    }


    public boolean canGoForward() {
        return !forwardSelections.isEmpty();
    }


    public void goForward() {
        if (!canGoForward()) {
            return;
        }
        initiatedSelection = true;
        // Pop of forward stack
        OWLEntity entity = forwardSelections.pop();
        prevSelections.push(curSel);
        selectionModel.setSelectedEntity(entity);
        initiatedSelection = false;
    }


    public void addChangeListener(ChangeListener changeListener) {
        changeListeners.add(changeListener);
    }


    public void removeChangeListener(ChangeListener changeListener) {
        changeListeners.remove(changeListener);
    }


    protected void fireStateChanged() {
        for (ChangeListener listener : new ArrayList<ChangeListener>(changeListeners)) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }
}
