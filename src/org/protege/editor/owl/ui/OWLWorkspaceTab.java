package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.workspace.WorkspaceTab;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 26-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class OWLWorkspaceTab extends WorkspaceTab {

    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getWorkspace().getEditorKit();
    }


    public OWLModelManager getOWLModelManager() {
        return getOWLEditorKit().getModelManager();
    }
}
