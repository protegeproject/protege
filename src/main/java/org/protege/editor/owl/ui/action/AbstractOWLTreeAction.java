package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 27, 2009<br><br>
 */
public abstract class AbstractOWLTreeAction<E extends OWLEntity> extends DisposableAction {

    private TreeSelectionModel selectionModel;

    private TreeSelectionListener selectionListener;


    public AbstractOWLTreeAction(String name, Icon icon, TreeSelectionModel selectionModel) {
        super(name, icon);
        this.selectionModel = selectionModel;
        this.selectionListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                reactToSelection();
            }
        };
        selectionModel.addTreeSelectionListener(selectionListener);
        setEnabled(canPerform(getSelectedOWLEntity()));
    }


    // Called on a selection history to enable/disable
    // the action.
    private void reactToSelection() {
        // Ask subclasses if we should be enabled for the current
        // selection
        setEnabled(canPerform(getSelectedOWLEntity()));
    }


    protected abstract boolean canPerform(E selEntity);


    protected TreePath getSelectionPath() {
        return selectionModel.getSelectionPath();
    }


    protected void setSelectionPath(TreePath path) {
        selectionModel.setSelectionPath(path);
    }


    /**
     * A convenience method that gets the first selected
     * <code>OWLClass</code>
     * @return The selected <code>OWLClass</code>, or
     *         <code>null</code> if no class is selected
     */
    protected E getSelectedOWLEntity() {
        TreePath treePath = selectionModel.getSelectionPath();
        if (treePath == null) {
            return null;
        }
        return (E) ((OWLObjectTreeNode<E>) treePath.getLastPathComponent()).getUserObject();
    }


    public void dispose() {
        selectionModel.removeTreeSelectionListener(selectionListener);
    }


    /**
     * The initialise method is called at the start of a
     * plugin instance life cycle.
     * This method is called to give the plugin a chance
     * to intitialise itself.  All plugin initialisation
     * should be done in this method rather than the plugin
     * constructor, since the initialisation might need to
     * occur at a point after plugin instance creation, and
     * a each plugin must have a zero argument constructor.
     */
    public void initialise() throws Exception {
    }
}