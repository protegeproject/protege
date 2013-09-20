package org.protege.editor.owl.ui.view.cls;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.action.DeleteClassAction;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.CreateNewSiblingTarget;
import org.protege.editor.owl.ui.view.CreateNewTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 21, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A view that contains a JTree that displays a
 * hierarchy of OWLClasses. The class hierarchy
 * is derived from told information.
 */
public class ToldOWLClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent
        implements CreateNewTarget, CreateNewChildTarget, CreateNewSiblingTarget {

    /**
     * 
     */
    private static final long serialVersionUID = 8712815067223088069L;


    public void performExtraInitialisation() throws Exception {
        // Add in the manipulation actions - we won't need to keep track
        // of these, as this will be done by the view - i.e. we won't
        // need to dispose of these actions.0

        addAction(new AbstractOWLTreeAction<OWLClass>("Add subclass", OWLIcons.getIcon("class.add.sub.png"),
                                                       getTree().getSelectionModel()){
        	private static final long serialVersionUID = -4067967212391062364L;
			
        	public void actionPerformed(ActionEvent event) {
                createNewChild();
            }
            protected boolean canPerform(OWLClass cls) {
                return canCreateNewChild();
            }
        }, "A", "A");

        addAction(new AbstractOWLTreeAction<OWLClass>("Add sibling class", OWLIcons.getIcon("class.add.sib.png"),
                                                 getTree().getSelectionModel()){

        	private static final long serialVersionUID = 9163133195546665441L;

        	public void actionPerformed(ActionEvent event) {
                createNewSibling();
            }
            protected boolean canPerform(OWLClass cls) {
                return canCreateNewSibling();
            }
        }, "A", "B");

        addAction(new DeleteClassAction(getOWLEditorKit(), new OWLEntitySetProvider<OWLClass>() {
            public Set<OWLClass> getEntities() {
                return new HashSet<OWLClass>(getTree().getSelectedOWLObjects());
            }
        }), "B", "A");

        getTree().setDragAndDropHandler(new OWLTreeDragAndDropHandler<OWLClass>() {
            public boolean canDrop(Object child, Object parent) {
                return child instanceof OWLClass;
            }


            public void move(OWLClass child, OWLClass fromParent, OWLClass toParent) {
                handleMove(child, fromParent, toParent);
            }


            public void add(OWLClass child, OWLClass parent) {
                handleAdd(child, parent);
            }
        });
    }


    private void handleAdd(OWLClass child, OWLClass parent) {
        if (child.equals(getOWLModelManager().getOWLDataFactory().getOWLThing())) {
            return;
        }
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
                                 df.getOWLDeclarationAxiom(child)));
        if (!df.getOWLThing().equals(parent)) {
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
                                     df.getOWLSubClassOfAxiom(child, parent)));
        }
        getOWLModelManager().applyChanges(changes);
    }


    private void handleMove(OWLClass child, OWLClass fromParent, OWLClass toParent) {
        final OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        if (child.equals(df.getOWLThing())) {
            return;
        }
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        // remove before adding in case the user is moving to the same class (or we could check)
        changes.add(new RemoveAxiom(getOWLModelManager().getActiveOntology(),
                					df.getOWLSubClassOfAxiom(child, fromParent)));
        if (!df.getOWLThing().equals(toParent)) {
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
                                     df.getOWLSubClassOfAxiom(child, toParent)));
        }
        getOWLModelManager().applyChanges(changes);
    }


    protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Create new target
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean canCreateNew() {
        return true;
    }


    public boolean canCreateNewChild() {
        return !getSelectedEntities().isEmpty();
    }


    public boolean canCreateNewSibling() {
        return !getSelectedEntities().isEmpty() &&
               !getSelectedEntity().equals(getOWLModelManager().getOWLDataFactory().getOWLThing());
    }


    public void createNewChild() {
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        if (set != null){
            OWLClass newClass = set.getOWLEntity();
            OWLClass selectedClass = getSelectedEntity();
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(set.getOntologyChanges());
            final OWLModelManager mngr = getOWLEditorKit().getModelManager();
            final OWLDataFactory df = mngr.getOWLDataFactory();
            if (!df.getOWLThing().equals(selectedClass)){
                OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(set.getOWLEntity(), selectedClass);
                changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            }
            mngr.applyChanges(changes);
            getTree().setSelectedOWLObject(newClass);
        }
    }


    public void createNewObject() {
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        if (set != null){
            OWLClass newClass = set.getOWLEntity();
            getOWLModelManager().applyChanges(set.getOntologyChanges());
            getTree().setSelectedOWLObject(newClass);
        }
    }


    public void createNewSibling() {
        OWLClass cls = getTree().getSelectedOWLObject();
        if (cls == null) {
            // Shouldn't really get here, because the
            // action should be disabled
            return;
        }
        // We need to apply the changes in the active ontology
        OWLEntityCreationSet<OWLClass> creationSet = getOWLWorkspace().createOWLClass();
        if (creationSet != null) {
        	OWLObjectTreeNode<OWLClass> parentNode 
        		= (OWLObjectTreeNode<OWLClass>) getTree().getSelectionPath().getParentPath().getLastPathComponent();
            if (parentNode == null || parentNode.getOWLObject() == null) {
            	return;
            }
        	OWLClass parentCls = parentNode.getOWLObject();
        	
        	// Combine the changes that are required to create the OWLClass, with the
            // changes that are required to make it a sibling class.
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(creationSet.getOntologyChanges());
            OWLModelManager mngr = getOWLModelManager();
            OWLDataFactory df = mngr.getOWLDataFactory();
            if (!df.getOWLThing().equals(parentCls)) {
                changes.add(new AddAxiom(mngr.getActiveOntology(), df.getOWLSubClassOfAxiom(creationSet.getOWLEntity(), parentCls)));
            }
            mngr.applyChanges(changes);
            // Select the new class
            getTree().setSelectedOWLObject(creationSet.getOWLEntity());
        }
    }
}
