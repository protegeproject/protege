package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 23, 2008
 */
public abstract class AbstractOWLPropertyHierarchyViewComponent<O extends OWLProperty> extends AbstractOWLEntityHierarchyViewComponent<O>
        implements Findable<O>, Deleteable, CreateNewChildTarget, CreateNewSiblingTarget {


    /**
     * 
     */
    private static final long serialVersionUID = 9069497093520748684L;


    protected abstract OWLSubPropertyAxiom getSubPropertyAxiom(O child, O parent);


    protected abstract boolean canAcceptDrop(Object child, Object parent);


    protected abstract OWLEntityCreationSet<O> createProperty();


    protected abstract Icon getSubIcon();


    protected abstract Icon getSibIcon();


    protected abstract Icon getDeleteIcon();


        protected void performExtraInitialisation() throws Exception {

        addAction(new AbstractOWLTreeAction<O>("Add sub property", getSubIcon(),
                                                                   getTree().getSelectionModel()){
            public void actionPerformed(ActionEvent event) {
                createNewChild();
            }
            protected boolean canPerform(O prop) {
                return canCreateNewChild();
            }
        }, "A", "A");

        addAction(new AbstractOWLTreeAction<O>("Add sibling property", getSibIcon(),
                                                                   getTree().getSelectionModel()){

            public void actionPerformed(ActionEvent event) {
                createNewSibling();
            }
            protected boolean canPerform(O cls) {
                return canCreateNewSibling();
            }
        }, "A", "B");

        addAction(new AbstractDeleteEntityAction<O>("Delete selected properties", getDeleteIcon(),
              getOWLEditorKit(),
              getHierarchyProvider(),
              new OWLEntitySetProvider<O>(){
                  public Set<O> getEntities() {
                      return getSelectedEntities();
                  }
              }){

            protected String getPluralDescription() {
                return "properties";
            }
        }, "B", "A");

        getTree().setDragAndDropHandler(new OWLPropertyTreeDropHandler<O>(getOWLModelManager()){
            protected OWLAxiom getAxiom(OWLDataFactory df, O child, O parent) {
                return getSubPropertyAxiom(child, parent);
            }


            public boolean canDrop(Object child, Object parent) {
                return canAcceptDrop(child, parent);
            }
        });
    }


    final protected OWLEntity updateView() {
        OWLProperty selProp = null;
        if (isOWLDataPropertyView()){
            selProp = updateView((O)getOWLWorkspace().getOWLSelectionModel().getLastSelectedDataProperty());
        }
        else if (isOWLObjectPropertyView()){
            selProp = updateView((O)getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty());
        }
        if (selProp != null) {
            updateRegisteredActions();
        }
        else {
            disableRegisteredActions();
        }
        return selProp;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Findable
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public java.util.List<O> find(String match) {
        if (isOWLDataPropertyView()){
            return new ArrayList<O>((Set<O>)getOWLModelManager().getOWLEntityFinder().getMatchingOWLDataProperties(match));
        }
        else if (isOWLObjectPropertyView()){
            return new ArrayList<O>((Set<O>)getOWLModelManager().getOWLEntityFinder().getMatchingOWLObjectProperties(match));
        }
        return Collections.emptyList();
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CreateNewChildTarget
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean canCreateNewChild() {
        return getSelectedEntity() != null;
    }


    public void createNewChild() {
        O selProp = getSelectedEntity();
        if (selProp == null) {
            return;
        }
        OWLEntityCreationSet<O> set = createProperty();
        if (set != null) {
            java.util.List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(set.getOntologyChanges());
            OWLAxiom ax = getSubPropertyAxiom(set.getOWLEntity(), selProp);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLModelManager().applyChanges(changes);
            getTree().setSelectedOWLObject(set.getOWLEntity());
        }
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // CreateNewSiblingTarget
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean canCreateNewSibling() {
        return getSelectedEntity() != null && !getSelectedEntity().equals(getTopProperty());
    }


    public void createNewSibling() {
        O property = getTree().getSelectedOWLObject();
        if (property == null) {
            // Shouldn't really get here, because the
            // action should be disabled
            return;
        }
        // We need to apply the changes in the active ontology
        OWLEntityCreationSet<O> creationSet = createProperty();
        if (creationSet != null) {
            // Combine the changes that are required to create the OWLAnnotationProperty, with the
            // changes that are required to make it a sibling property.
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(creationSet.getOntologyChanges());
            OWLOntology ont = getOWLModelManager().getActiveOntology();
            for (O par : getHierarchyProvider().getParents(property)) {
                OWLAxiom ax = getSubPropertyAxiom(creationSet.getOWLEntity(), par);
                changes.add(new AddAxiom(ont, ax));
            }
            getOWLModelManager().applyChanges(changes);
            getTree().setSelectedOWLObject(creationSet.getOWLEntity());
        }
    }

    
    private O getTopProperty() {
        return getHierarchyProvider().getRoots().iterator().next();
    }
}
