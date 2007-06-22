package org.protege.editor.owl.ui.navigation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.selection.OWLSelectionHistoryManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
public class OWLEntityNavigationMediator {

    private OWLEditorKit owlEditorKit;

    private Action backAction;

    private Action forwardAction;

    private ChangeListener listener;


    public OWLEntityNavigationMediator(OWLEditorKit owlEditorKit, Action backAction, Action forwardAction) {
        this.owlEditorKit = owlEditorKit;
        this.backAction = backAction;
        this.forwardAction = forwardAction;
        listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateActionState();
            }
        };
        owlEditorKit.getOWLWorkspace().getOWLSelectionHistoryManager().addChangeListener(listener);
        updateActionState();
    }


    public void dispose() {
        owlEditorKit.getOWLWorkspace().getOWLSelectionHistoryManager().removeChangeListener(listener);
    }


    private void updateActionState() {
        OWLSelectionHistoryManager historyManager = owlEditorKit.getOWLWorkspace().getOWLSelectionHistoryManager();
        backAction.setEnabled(historyManager.canGoBack());
        forwardAction.setEnabled(historyManager.canGoForward());
    }
}
