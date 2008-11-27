package org.protege.editor.owl.ui.clshierarchy;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.CreateNewSiblingTarget;
import org.protege.editor.owl.ui.view.CreateNewTarget;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntitySetProvider;

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
public class ToldOWLClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent implements CreateNewTarget, CreateNewChildTarget, CreateNewSiblingTarget {

    public void performExtraInitialisation() throws Exception {
        // Add in the manipulation actions - we won't need to keep track
        // of these, as this will be done by the view - i.e. we won't
        // need to dispose of these actions.
        addAction(new AddSubClassAction(getOWLEditorKit(), getTree()), "A", "A");
        addAction(new AddSiblingClassAction(getOWLEditorKit(), getTree()), "A", "B");
        addAction(new DeleteClassAction(getOWLEditorKit(), new OWLEntitySetProvider<OWLClass>() {
            public Set<OWLClass> getEntities() {
                return new HashSet<OWLClass>(getTree().getSelectedOWLObjects());
            }
        }), "B", "A");

        getTree().setDragAndDropHandler(new OWLTreeDragAndDropHandler<OWLClass>() {
            public void move(OWLClass child, OWLClass fromParent, OWLClass toParent) {
                if (child.equals(getOWLModelManager().getOWLDataFactory().getOWLThing())) {
                    return;
                }
                List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
                                         getOWLModelManager().getOWLDataFactory().getOWLSubClassAxiom(child,
                                                                                                      toParent)));
                changes.add(new RemoveAxiom(getOWLModelManager().getActiveOntology(),
                                            getOWLModelManager().getOWLDataFactory().getOWLSubClassAxiom(child,
                                                                                                         fromParent)));
                getOWLModelManager().applyChanges(changes);
            }


            public void add(OWLClass child, OWLClass parent) {
                if (child.equals(getOWLModelManager().getOWLDataFactory().getOWLThing())) {
                    return;
                }
                List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(),
                                         getOWLModelManager().getOWLDataFactory().getOWLSubClassAxiom(child, parent)));
                getOWLModelManager().applyChanges(changes);
            }
        });
    }


    protected OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
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
        return !getSelectedClasses().isEmpty();
    }


    public boolean canCreateNewSibling() {
        return !getSelectedClasses().isEmpty();
    }


    public void createNewChild() {
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        if (set != null){
            OWLClass newClass = set.getOWLEntity();
            OWLClass selectedClass = getSelectedClass();
            OWLSubClassAxiom ax = getOWLDataFactory().getOWLSubClassAxiom(newClass, selectedClass);
            getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(newClass);
        }
    }


    public void createNewObject() {
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        if (set != null){
            OWLClass newClass = set.getOWLEntity();
            OWLClass selectedClass = getOWLDataFactory().getOWLThing();
            OWLSubClassAxiom ax = getOWLDataFactory().getOWLSubClassAxiom(newClass, selectedClass);
            getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(newClass);
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
            // Combine the changes that are required to create the OWLClass, with the
            // changes that are required to make it a sibling class.
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(creationSet.getOntologyChanges());
            OWLModelManager owlModelManager = getOWLModelManager();
            for (OWLClass par : owlModelManager.getOWLHierarchyManager().getOWLClassHierarchyProvider().getParents(cls)) {
                OWLDataFactory df = owlModelManager.getOWLDataFactory();
                OWLAxiom ax = df.getOWLSubClassAxiom(creationSet.getOWLEntity(), par);
                changes.add(new AddAxiom(owlModelManager.getActiveOntology(), ax));
            }
            owlModelManager.applyChanges(changes);
            // Select the new class
            getTree().setSelectedOWLObject(creationSet.getOWLEntity());
        }
    }
}
