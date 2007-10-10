package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLWorkspaceViewsTab extends WorkspaceViewsTab {

    public OWLModelManager getOWLModelManager() {
        return (OWLModelManager) getWorkspace().getEditorKit().getModelManager();
    }


    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getWorkspace().getEditorKit();
    }
}
