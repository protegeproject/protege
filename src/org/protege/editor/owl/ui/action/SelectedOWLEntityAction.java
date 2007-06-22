package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owl.model.OWLEntity;

import java.awt.event.ActionEvent;
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
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 * <p/>
 * A base class for actions which perform an action based on the selected entity.
 */
public abstract class SelectedOWLEntityAction extends ProtegeOWLAction {

    private static final Logger logger = Logger.getLogger(SelectedOWLEntityAction.class);

    private OWLSelectionModelListener listener;


    final public void actionPerformed(ActionEvent e) {
        actionPerformed(getSelectedEntity());
    }


    protected OWLEntity getSelectedEntity() {
        return getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
    }


    protected abstract void actionPerformed(OWLEntity selectedEntity);


    final public void initialise() throws Exception {
        listener = new OWLSelectionModelListener() {
            public void selectionChanged() throws Exception {
                updateState();
            }
        };
        updateState();
        getOWLWorkspace().getOWLSelectionModel().addListener(listener);
    }


    private void updateState() {
        setEnabled(getSelectedEntity() != null);
    }


    final public void dispose() throws Exception {
        getOWLWorkspace().getOWLSelectionModel().removeListener(listener);
        disposeAction();
    }


    protected abstract void disposeAction() throws Exception;
}
