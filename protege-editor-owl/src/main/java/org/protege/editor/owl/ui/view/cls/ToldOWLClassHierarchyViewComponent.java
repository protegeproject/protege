package org.protege.editor.owl.ui.view.cls;

import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.selection.SelectionDriver;
import org.protege.editor.owl.ui.action.AbstractOWLTreeAction;
import org.protege.editor.owl.ui.action.DeleteClassAction;
import org.protege.editor.owl.ui.renderer.AddChildIcon;
import org.protege.editor.owl.ui.renderer.AddSiblingIcon;
import org.protege.editor.owl.ui.renderer.OWLClassIcon;
import org.protege.editor.owl.ui.tree.OWLObjectTreeNode;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.tree.OWLTreePreferences;
import org.protege.editor.owl.ui.view.CreateNewChildTarget;
import org.protege.editor.owl.ui.view.CreateNewSiblingTarget;
import org.protege.editor.owl.ui.view.CreateNewTarget;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 21, 2006<br><br>
 * <p>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p>
 * A view that contains a JTree that displays a
 * hierarchy of OWLClasses. The class hierarchy
 * is derived from told information.
 */
public class ToldOWLClassHierarchyViewComponent extends AbstractOWLClassHierarchyViewComponent
        implements CreateNewTarget, CreateNewChildTarget, CreateNewSiblingTarget, SelectionDriver {

    private static final OWLClassIcon OWL_CLASS_ICON = new OWLClassIcon();

    private static final Icon ADD_SUB_ICON = new AddChildIcon(OWL_CLASS_ICON);

    private static final Icon ADD_SIBLING_ICON = new AddSiblingIcon(OWL_CLASS_ICON);

    private static final String ADD_GROUP = "A";

    private static final String DELETE_GROUP = "B";

    private static final String FIRST_SLOT = "A";

    private static final String SECOND_SLOT = "B";


    public void performExtraInitialisation() throws Exception {
        // Add in the manipulation actions - we won't need to keep track
        // of these, as this will be done by the view - i.e. we won't
        // need to dispose of these actions.

        AbstractOWLTreeAction<OWLClass> addSubClassAction =
                new AbstractOWLTreeAction<OWLClass>("Add subclass",
                                                    ADD_SUB_ICON,
                                                    getTree().getSelectionModel()) {
                    public void actionPerformed(ActionEvent event) {
                        createNewChild();
                    }

                    protected boolean canPerform(OWLClass cls) {
                        return canCreateNewChild();
                    }
                };

        addAction(addSubClassAction, ADD_GROUP, FIRST_SLOT);

        AbstractOWLTreeAction<OWLClass> addSiblingClassAction =
                new AbstractOWLTreeAction<OWLClass>("Add sibling class",
                                                    ADD_SIBLING_ICON,
                                                    getTree().getSelectionModel()) {
                    public void actionPerformed(ActionEvent event) {
                        createNewSibling();
                    }

                    protected boolean canPerform(OWLClass cls) {
                        return canCreateNewSibling();
                    }
                };

        addAction(addSiblingClassAction, ADD_GROUP, SECOND_SLOT);

        DeleteClassAction deleteClassAction =
                new DeleteClassAction(getOWLEditorKit(),
                                      () -> new HashSet<>(getTree().getSelectedOWLObjects())) {
                    @Override
                    public void updateState() {
                        super.updateState();
                        if (isEnabled()) {
                            setEnabled(isInAssertedMode());
                        }
                    }
                };

        addAction(deleteClassAction, DELETE_GROUP, FIRST_SLOT);

        getTree().setDragAndDropHandler(new OWLTreeDragAndDropHandler<OWLClass>() {
            public boolean canDrop(Object child, Object parent) {
                return OWLTreePreferences.getInstance().isTreeDragAndDropEnabled() && child instanceof OWLClass;
            }


            public void move(OWLClass child, OWLClass fromParent, OWLClass toParent) {
                if (!OWLTreePreferences.getInstance().isTreeDragAndDropEnabled()) {
                    return;
                }
                handleMove(child, fromParent, toParent);
            }


            public void add(OWLClass child, OWLClass parent) {
                if (!OWLTreePreferences.getInstance().isTreeDragAndDropEnabled()) {
                    return;
                }
                handleAdd(child, parent);
            }
        });
        getAssertedTree().setPopupMenuId(new PopupMenuId("[AssertedClassHierarchy]"));
    }

    private void handleAdd(OWLClass child, OWLClass parent) {
        if (child.equals(getOWLModelManager().getOWLDataFactory().getOWLThing())) {
            return;
        }
        List<OWLOntologyChange> changes = new ArrayList<>();
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
        List<OWLOntologyChange> changes = new ArrayList<>();
        // remove before adding in case the user is moving to the same class (or we could check)

        OWLOntology activeOntology = getOWLModelManager().getActiveOntology();
        OWLSubClassOfAxiom existingNonAnnotatedAxiom = df.getOWLSubClassOfAxiom(child, fromParent);
        if (fromParent.isOWLThing() && activeOntology.getAxiomsIgnoreAnnotations(existingNonAnnotatedAxiom,
                                                                                 Imports.INCLUDED).isEmpty()) {
            // Top level class without any explicit subclass of axiom
            if (!toParent.isOWLThing()) {
                changes.add(new AddAxiom(activeOntology, df.getOWLSubClassOfAxiom(child, toParent)));
            }
        }
        else {
            for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
                Set<OWLAxiom> axiomsToRemove = ont.getAxiomsIgnoreAnnotations(existingNonAnnotatedAxiom);
                for (OWLAxiom ax : axiomsToRemove) {
                    changes.add(new RemoveAxiom(ont, ax));
                    // Preserve the annotations.  If there are no annotations then don't add it if it's a subclass of
                    // owl:Thing
                    if (!ax.getAnnotations().isEmpty() || !toParent.isOWLThing()) {
                        OWLAxiom axToAdd = df.getOWLSubClassOfAxiom(child, toParent, ax.getAnnotations());
                        changes.add(new AddAxiom(ont, axToAdd));
                    }
                }
            }
        }

        getOWLModelManager().applyChanges(changes);
    }


    protected OWLObjectHierarchyProvider<OWLClass> getHierarchyProvider() {
        return getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
    }

    @Override
    protected Optional<OWLObjectHierarchyProvider<OWLClass>> getInferredHierarchyProvider() {
        return Optional.of(getOWLModelManager().getOWLHierarchyManager().getInferredOWLClassHierarchyProvider());
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Create new target
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean canCreateNew() {
        return isInAssertedMode();
    }


    public boolean canCreateNewChild() {
        return isInAssertedMode() &&
                !getSelectedEntities().isEmpty();
    }

    public boolean canCreateNewSibling() {
        return isInAssertedMode() &&
                !getSelectedEntities().isEmpty() &&
                !getSelectedEntity().equals(getOWLModelManager().getOWLDataFactory().getOWLThing());
    }


    public void createNewChild() {
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        if (set != null) {
            OWLClass newClass = set.getOWLEntity();
            OWLClass selectedClass = getSelectedEntity();
            List<OWLOntologyChange> changes = new ArrayList<>();
            changes.addAll(set.getOntologyChanges());
            final OWLModelManager mngr = getOWLEditorKit().getModelManager();
            final OWLDataFactory df = mngr.getOWLDataFactory();
            if (!df.getOWLThing().equals(selectedClass)) {
                OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(set.getOWLEntity(), selectedClass);
                changes.add(new AddAxiom(mngr.getActiveOntology(), ax));
            }
            mngr.applyChanges(changes);
            getTree().setSelectedOWLObject(newClass);
        }
    }


    public void createNewObject() {
        OWLEntityCreationSet<OWLClass> set = getOWLWorkspace().createOWLClass();
        if (set != null) {
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
            List<OWLOntologyChange> changes = new ArrayList<>();
            changes.addAll(creationSet.getOntologyChanges());
            OWLModelManager mngr = getOWLModelManager();
            OWLDataFactory df = mngr.getOWLDataFactory();
            if (!df.getOWLThing().equals(parentCls)) {
                changes.add(new AddAxiom(mngr.getActiveOntology(),
                                         df.getOWLSubClassOfAxiom(creationSet.getOWLEntity(), parentCls)));
            }
            mngr.applyChanges(changes);
            // Select the new class
            getTree().setSelectedOWLObject(creationSet.getOWLEntity());
        }
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public Optional<OWLObject> getSelection() {
        return Optional.ofNullable(getSelectedEntity());
    }

    @Override
    public void disposeView() {
        super.disposeView();
    }
}
