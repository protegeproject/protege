package org.protege.editor.core.ui.action;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.core.ui.workspace.Workspace;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 27, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An action that is associated with a particular
 * <code>EditorKit</code>.  Actions are used in menu
 * items and toolbar items.
 */
public abstract class ProtegeAction extends AbstractAction implements ProtegePluginInstance {
    private static final long serialVersionUID = -2813521478656046404L;
    private EditorKit editorKit;


    protected EditorKit getEditorKit() {
        return editorKit;
    }


    protected Workspace getWorkspace() {
        return editorKit.getWorkspace();
    }


    public void setEditorKit(EditorKit editorKit) {
        this.editorKit = editorKit;
    }
}
