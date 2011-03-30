package org.protege.editor.core.editorkit;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.workspace.WorkspaceManager;


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


    public List<EditorKit> getEditorKits(){
        return new ArrayList<EditorKit>(editorKits);
    }
}
