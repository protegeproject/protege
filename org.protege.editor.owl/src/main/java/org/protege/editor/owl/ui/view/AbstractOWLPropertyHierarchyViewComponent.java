package org.protege.editor.owl.ui.view;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.action.AbstractDeleteEntityAction;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLSubPropertyAxiom;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;

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

        addAction(new AbstractOWLTreeAction<O>("Add sub property", 
        									   getSubIcon(),
                                               getTree().getSelectionModel()) {
			private static final long serialVersionUID = -1108739210585116570L;
			public void actionPerformed(ActionEvent event) {
                createNewChild();
            }
            protected boolean canPerform(O prop) {
                return canCreateNewChild();
            }
        }, "A", "A");

        addAction(new AbstractOWLTreeAction<O>("Add sibling property", 
        		                               getSibIcon(),
                                               getTree().getSelectionModel()){

        	private static final long serialVersionUID = 29239289622664679L;
			public void actionPerformed(ActionEvent event) {
                createNewSibling();
            }
            protected boolean canPerform(O cls) {
                return canCreateNewSibling();
            }
        }, "A", "B");

        addAction(new AbstractDeleteEntityAction<O>("Delete selected properties", 
		         									getDeleteIcon(),
		         									getOWLEditorKit(),
		         									getHierarchyProvider(),
		         									new OWLEntitySetProvider<O>(){
        	                                           public Set<O> getEntities() {
        	                                        	   return getSelectedEntities();
        	                                           }
                                                     }) {

        	private static final long serialVersionUID = -2505868423392875972L;

        	protected String getPluralDescription() {
        		return "properties";
        	}

        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (!getTopProperty().equals(getSelectedEntity())) {
        			super.actionPerformed(e);
				}
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
        O selectedProperty = getSelectedEntity();
        if (selectedProperty == null) {
            return;
        }
        OWLEntityCreationSet<O> set = createProperty();
        if (set != null) {
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(set.getOntologyChanges());
            if (shouldAddAsParentOfNewlyCreatedProperty(selectedProperty)) {
            	OWLAxiom ax = getSubPropertyAxiom(set.getOWLEntity(), selectedProperty);
            	changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
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
            for (O parentProperty : getHierarchyProvider().getParents(property)) {
            	if (shouldAddAsParentOfNewlyCreatedProperty(parentProperty)) {  
            		OWLAxiom ax = getSubPropertyAxiom(creationSet.getOWLEntity(), parentProperty);
            		changes.add(new AddAxiom(ont, ax));
            	}
            }
            getOWLModelManager().applyChanges(changes);
            getTree().setSelectedOWLObject(creationSet.getOWLEntity());
        }
    }
    
    
    /*
     * This code will get me into trouble if and when a hierarchy does not have owl:topObject/DataProperty
     * as the root of the hierarchy.  I don't know if this is possible yet but it can be imagined. By adding 
     * a protected method we allow for the possibility that this behavior can be overridden.
     */
    protected boolean shouldAddAsParentOfNewlyCreatedProperty(O parent) {
    	return !getHierarchyProvider().getRoots().contains(parent);
    }

    
    private O getTopProperty() {
        return getHierarchyProvider().getRoots().iterator().next();
    }
}
