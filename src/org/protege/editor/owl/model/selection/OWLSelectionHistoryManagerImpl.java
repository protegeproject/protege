package org.protege.editor.owl.model.selection;

import org.semanticweb.owl.model.OWLEntity;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
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

        listener = new OWLSelectionModelListener() {
            public void selectionChanged() throws Exception {
                handleSelection();
            }
        };
        selectionModel.addListener(listener);
    }


    private void handleSelection() {
        OWLEntity entity = selectionModel.getSelectedEntity();
        if (entity == null) {
            return;
        }
        if (!initiatedSelection) {
            // Record selection
            if (curSel != null) {
                prevSelections.push(curSel);
            }
            forwardSelections.clear();
        }
        curSel = entity;
        fireStateChanged();
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
