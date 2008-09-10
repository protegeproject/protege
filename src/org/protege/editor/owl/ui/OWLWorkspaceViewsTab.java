package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEntityDisplayProvider;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;


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

    private OWLEntityDisplayProvider provider = new OWLEntityDisplayProvider() {
        public boolean canDisplay(OWLEntity owlEntity) {
            return OWLWorkspaceViewsTab.this.canDisplay(owlEntity);
        }

        public JComponent getDisplayComponent() {
            return OWLWorkspaceViewsTab.this;
        }
    };


    private boolean canDisplay(OWLEntity owlEntity) {
        // search the contained views to see if there is one that can show the entity
        
        for (View view : getViewsPane().getViews()){
            ViewComponent vc = view.getViewComponent();
            if (vc instanceof AbstractOWLSelectionViewComponent){
                final AbstractOWLSelectionViewComponent owlEntityViewComponent = (AbstractOWLSelectionViewComponent)vc;
                if (owlEntityViewComponent.canShowEntity(owlEntity)){
                    return true;
                }
            }
        }
        return false;
    }


    public void initialise() {
        super.initialise();
        getOWLEditorKit().getWorkspace().registerOWLEntityDisplayProvider(provider);
    }


    public void dispose() {
        getOWLEditorKit().getWorkspace().unregisterOWLEntityDisplayProvider(provider);
        super.dispose();
    }


    public OWLModelManager getOWLModelManager() {
        return (OWLModelManager) getWorkspace().getEditorKit().getModelManager();
    }


    public OWLEditorKit getOWLEditorKit() {
        return (OWLEditorKit) getWorkspace().getEditorKit();
    }
}
