package org.protege.editor.owl.ui;

import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLEntityDisplayProvider;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.semanticweb.owlapi.model.*;

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

        String entityCat = new NavFinder().getNav(owlEntity);

        // search the contained views to see if there is one that can show the entity
        for (View view : getViewsPane().getViews()){
            ViewComponent vc = view.getViewComponent();
            if (vc != null){ // if the view is on a tab that has been initialised ask it directly
                if (vc instanceof AbstractOWLSelectionViewComponent){
                    final AbstractOWLSelectionViewComponent owlEntityViewComponent = (AbstractOWLSelectionViewComponent)vc;
                    if (owlEntityViewComponent.canShowEntity(owlEntity)){
                        return true;
                    }
                }
            }
            else { // otherwise, ask its plugin
                ViewComponentPlugin plugin = getWorkspace().getViewManager().getViewComponentPlugin(view.getId());
                if (plugin != null) {
                    for (String nav : plugin.getNavigates()){
                        if (entityCat.equals(nav)){
                            return true;
                        }
                    }
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


    class NavFinder implements OWLEntityVisitor{

        private String nav;


        public String getNav(OWLEntity owlEntity) {
            nav = null;
            owlEntity.accept(this);
            return nav;
        }


        public void visit(OWLClass owlClass) {
            nav = ProtegeProperties.getInstance().getProperty(ProtegeProperties.CLASS_VIEW_CATEGORY);
        }


        public void visit(OWLObjectProperty owlObjectProperty) {
            nav = ProtegeProperties.getInstance().getProperty(ProtegeProperties.OBJECT_PROPERTY_VIEW_CATEGORY);
        }


        public void visit(OWLDataProperty owlDataProperty) {
            nav = ProtegeProperties.getInstance().getProperty(ProtegeProperties.DATA_PROPERTY_VIEW_CATEGORY);
        }

        
        public void visit(OWLAnnotationProperty owlAnnotationProperty) {
            nav = ProtegeProperties.getInstance().getProperty(ProtegeProperties.ANNOTATION_PROPERTY_VIEW_CATEGORY);
        }


        public void visit(OWLNamedIndividual owlIndividual) {
            nav = ProtegeProperties.getInstance().getProperty(ProtegeProperties.INDIVIDUAL_VIEW_CATEGORY);
        }


        public void visit(OWLDatatype owlDatatype) {
            nav = ProtegeProperties.getInstance().getProperty(ProtegeProperties.DATATYPE_VIEW_CATEGORY);
        }
    }
}
