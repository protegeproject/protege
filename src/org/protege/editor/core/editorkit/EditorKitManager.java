package org.protege.editor.core.editorkit;

import org.protege.editor.core.ui.workspace.WorkspaceManager;

import java.util.ArrayList;
import java.util.List;
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
 * Date: Mar 15, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EditorKitManager {

    private EditorKit activeEditorKit;

    private List<EditorKit> editorKits;

    private WorkspaceManager workspaceManager;


    public EditorKitManager() {
        editorKits = new ArrayList<EditorKit>();
        workspaceManager = new WorkspaceManager();
    }


    /**
     * Adds a new <code>EditorKit</code> to the list of
     * open <code>EditorKits</code>.  This will result in
     * the <code>EditorKit</code>'s <code>Workspace</code>
     * added to the UI.  The <code>Workspace</code> will then be
     * selected.
     * @param editorKit The <code>EditorKit</code> to be added.
     *                  If the manager already has a reference to the specified
     *                  <code>EditorKit</code> then this method doesn't add the
     *                  <code>EditorKit</code> again.
     */
    public void addEditorKit(EditorKit editorKit) {
        if (!editorKits.contains(editorKit)) {
            editorKits.add(editorKit);
            workspaceManager.addWorkspace(editorKit.getWorkspace());
        }
    }


    /**
     * Removes an open <code>EditorKit</code>.  The
     * corresponding <code>Workspace</code> will be removed
     * from the UI. If the
     * <code>EditorKit</code> is not open then this method
     * does nothing.
     */
    public void removeEditorKit(EditorKit editorKit) {
        editorKits.remove(editorKit);
        workspaceManager.removeWorkspace(editorKit.getWorkspace());
    }


    /**
     * Gets the <code>WorkspaceManager</code> that corresponds
     * to this <code>EditorKitManager</code>.  The <code>WorkspaceManager</code>
     * is used to manage the "view" part of the clsdescriptioneditor kit model view controller.
     */
    public WorkspaceManager getWorkspaceManager() {
        return workspaceManager;
    }


    /**
     * Gets the number of editor kits which are being managed
     * by this manager.
     */
    public int getEditorKitCount() {
        return editorKits.size();
    }
}
