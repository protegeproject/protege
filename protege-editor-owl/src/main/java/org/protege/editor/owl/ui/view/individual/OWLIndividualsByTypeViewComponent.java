package org.protege.editor.owl.ui.view.individual;

import com.google.common.collect.Sets;

import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.IndividualsByTypeHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProviderListener;
import org.protege.editor.owl.model.selection.SelectionDriver;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.action.DeleteIndividualAction;
import org.protege.editor.owl.ui.renderer.AddEntityIcon;
import org.protege.editor.owl.ui.renderer.OWLClassIcon;
import org.protege.editor.owl.ui.renderer.OWLIndividualIcon;
import org.protege.editor.owl.ui.tree.*;
import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.protege.editor.owl.ui.view.CreateNewTarget;
import org.protege.editor.owl.ui.view.Findable;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-May-2007<br><br>
 */
public class OWLIndividualsByTypeViewComponent extends AbstractOWLSelectionViewComponent
        implements Findable<OWLNamedIndividual>, CreateNewTarget, RefreshableComponent, SelectionDriver {


    private OWLObjectTree<OWLObject> tree;

    private ChangeListenerMediator changeListenerMediator;


    private TreeSelectionListener listener = e -> transmitSelection();

    private HierarchyProviderWrapper hierarchyProvider;


    public void initialiseView() throws Exception {
        setLayout(new BorderLayout());
        hierarchyProvider = new HierarchyProviderWrapper();

        tree = new OWLModelManagerTree<>(getOWLEditorKit(), hierarchyProvider);
        tree.setCellRenderer(new CountingOWLObjectTreeCellRenderer<>(getOWLEditorKit(), tree));
        tree.expandRow(0);
        add(new JScrollPane(tree));

        changeListenerMediator = new ChangeListenerMediator();

        tree.addTreeSelectionListener(listener);

        tree.setDragAndDropHandler(new OWLTreeDragAndDropHandler<OWLObject>() {

            public boolean canDrop(Object child, Object parent) {
                return child instanceof OWLNamedIndividual &&
                       parent instanceof OWLClass;
            }

            public void move(OWLObject child, OWLObject fromParent, OWLObject toParent) {
                handleMove(child, fromParent, toParent);
            }

            public void add(OWLObject child, OWLObject parent) {
                handleAdd(child, parent);
            }
        });
        setupActions();
        tree.setPopupMenuId(new PopupMenuId("[IndividualHierarchy]"));
    }


    private IndividualsByTypeHierarchyProvider getProvider(){
        return getOWLModelManager().getOWLHierarchyManager().getOWLIndividualsByTypeHierarchyProvider();
    }


    private void handleAdd(OWLObject child, OWLObject toParent) {
        if (child instanceof OWLNamedIndividual){
            OWLNamedIndividual ind = (OWLNamedIndividual)child;

            List<OWLOntologyChange> changes = new ArrayList<>();

            if (toParent != null && toParent instanceof OWLClass){
                OWLClass to = (OWLClass)toParent;
                OWLClassAssertionAxiom ax = getOWLModelManager().getOWLDataFactory().getOWLClassAssertionAxiom(to, ind);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            getOWLModelManager().applyChanges(changes);
        }
    }


    private void handleMove(OWLObject child, OWLObject fromParent, OWLObject toParent) {
        if (child instanceof OWLNamedIndividual){
            OWLNamedIndividual ind = (OWLNamedIndividual)child;

            List<OWLOntologyChange> changes = new ArrayList<>();

            if (toParent != null && toParent instanceof OWLClass){
                OWLClass to = (OWLClass)toParent;
                OWLClassAssertionAxiom ax = getOWLModelManager().getOWLDataFactory().getOWLClassAssertionAxiom(to, ind);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }

            if (fromParent != null && fromParent instanceof OWLClass){
                OWLClass from = (OWLClass)fromParent;
                OWLClassAssertionAxiom ax = getOWLModelManager().getOWLDataFactory().getOWLClassAssertionAxiom(from, ind);
                for (OWLOntology ont : getOWLModelManager().getActiveOntologies()){
                    if (ont.containsAxiom(ax)){
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                }
            }

            getOWLModelManager().applyChanges(changes);
        }
    }


    protected void setupActions() {
        addAction(new DisposableAction("Add individual", new AddEntityIcon(new OWLIndividualIcon())) {
            public void actionPerformed(ActionEvent e) {
                createNewObject();
            }

            public void dispose() {
            }
        } , "A", "A");
        addAction(new DeleteIndividualAction(getOWLEditorKit(), () -> getSelectedIndividuals()), "B", "A");
        addAction(new DisposableAction("Add empty class to list", new AddEntityIcon(new OWLClassIcon())) {
            @Override
            public void dispose() {

            }

            @Override
            public void actionPerformed(ActionEvent e) {
                addEmptyClassToList();
            }
        }, "C", "A");
    }

    private void addEmptyClassToList() {
        UIHelper helper = new UIHelper(getOWLEditorKit());
        OWLClass cls = helper.pickOWLClass();
        if(cls != null) {
            hierarchyProvider.addTemporaryClass(cls);
        }
    }


    public void disposeView() {
        tree.dispose();
    }


    protected boolean isOWLClassView() {
        return true;
    }


    protected boolean isOWLIndividualView() {
        return true;
    }

    final protected OWLObject updateView() {
        OWLObject sel = null;
        OWLEntity entity = getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        if (entity instanceof OWLClass || entity instanceof OWLNamedIndividual){
            sel = updateView(entity);
            if (sel != null) {
                updateRegisteredActions();
            }
            else {
                disableRegisteredActions();
            }
        }
        return sel;
    }


    private Set<OWLNamedIndividual> getSelectedIndividuals(){
        List<OWLObject> sel = tree.getSelectedOWLObjects();
        Set<OWLNamedIndividual> selIndivs = new HashSet<>();
        for (OWLObject obj : sel){
            if (obj instanceof OWLNamedIndividual){
                selIndivs.add((OWLNamedIndividual)obj);
            }
        }
        return selIndivs;
    }


    private OWLObject updateView(OWLObject selectedEntity) {
        OWLObject selObj = tree.getSelectedOWLObject();
        if (selectedEntity != null && selObj != null) {
            if (selectedEntity.equals(selObj)) {
                return selectedEntity;
            }
        }
        tree.setSelectedOWLObject(selectedEntity);
        return selectedEntity;
    }


    private void transmitSelection() {
        if (isSynchronizing()){
            OWLObject obj = tree.getSelectedOWLObject();
            if (obj instanceof OWLEntity) {
                setGlobalSelection((OWLEntity) obj);
            }
            else {
                setGlobalSelection(null);
            }
        }
        changeListenerMediator.fireStateChanged(this);
    }


    //////// Findable

    public List<OWLNamedIndividual> find(String match) {
        return new ArrayList<>(getOWLModelManager().getOWLEntityFinder().getMatchingOWLIndividuals(match));
    }

    public void show(OWLNamedIndividual owlEntity) {
        tree.setSelectedOWLObject(owlEntity);
    }


    //////// Deleteable

    public boolean canDelete() {
        return !getSelectedIndividuals().isEmpty();
    }


    public void handleDelete() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public boolean canCreateNew() {
        return true;
    }


    public void createNewObject() {
        OWLEntityCreationSet<OWLNamedIndividual> set = getOWLWorkspace().createOWLIndividual();
        if (set == null) {
            return;
        }
        java.util.List<OWLOntologyChange> changes = new ArrayList<>();
        changes.addAll(set.getOntologyChanges());
        TreePath selectionPath = tree.getSelectionPath();
        OWLClass targetType = null;
        while(selectionPath != null) {
            if(selectionPath.getLastPathComponent() instanceof OWLObjectTreeNode) {
                OWLObjectTreeNode node = (OWLObjectTreeNode) selectionPath.getLastPathComponent();
                if(node.getOWLObject() instanceof OWLClass) {
                    targetType = (OWLClass) node.getOWLObject();
                    break;
                }
            }
            selectionPath = selectionPath.getParentPath();
        }
        if(targetType != null) {
            OWLOntology ont = getOWLModelManager().getActiveOntology();
            changes.add(new AddAxiom(ont, getOWLDataFactory().getOWLClassAssertionAxiom(targetType, set.getOWLEntity())));
        }
        getOWLModelManager().applyChanges(changes);
        OWLNamedIndividual ind = set.getOWLEntity();
        if (ind != null) {
            tree.setSelectedOWLObject(ind, false);
        }
    }


    public void addChangeListener(ChangeListener listener) {
        changeListenerMediator.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListenerMediator.removeChangeListener(listener);
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public Optional<OWLObject> getSelection() {
        return Optional.ofNullable(tree.getSelectedOWLObject());
    }



    private class HierarchyProviderWrapper implements OWLObjectHierarchyProvider<OWLObject> {

        private final Set<OWLObject> temporaryClasses = new HashSet<>();

        private final List<OWLObjectHierarchyProviderListener<OWLObject>> listeners = new ArrayList<>();

        public void addTemporaryClass(OWLClass cls) {
            temporaryClasses.add(cls);
            for(OWLObjectHierarchyProviderListener<OWLObject> listener : listeners) {
                listener.nodeChanged(cls);
            }
        }

        @Override
        public void setOntologies(Set<OWLOntology> ontologies) {
            getProvider().setOntologies(ontologies);
        }

        @Override
        public Set<OWLObject> getRoots() {
            Sets.SetView<OWLObject> roots = Sets.union(getProvider().getRoots(), temporaryClasses);
            return roots;
        }

        @Override
        public Set<OWLObject> getChildren(OWLObject object) {
            return getProvider().getChildren(object);
        }

        @Override
        public Set<OWLObject> getDescendants(OWLObject object) {
            return getProvider().getDescendants(object);
        }

        @Override
        public Set<OWLObject> getParents(OWLObject object) {
            if(temporaryClasses.contains(object)) {
                return Collections.emptySet();
            }
            return getProvider().getParents(object);
        }

        @Override
        public Set<OWLObject> getAncestors(OWLObject object) {
            return getProvider().getAncestors(object);
        }

        @Override
        public Set<OWLObject> getEquivalents(OWLObject object) {
            return getProvider().getEquivalents(object);
        }

        @Override
        public Set<List<OWLObject>> getPathsToRoot(OWLObject object) {
            if(temporaryClasses.contains(object)) {
                return Collections.singleton(Collections.singletonList(object));
            }
            return getProvider().getPathsToRoot(object);
        }

        @Override
        public boolean containsReference(OWLObject object) {
            return temporaryClasses.contains(object) || getProvider().containsReference(object);
        }

        @Override
        public void addListener(OWLObjectHierarchyProviderListener<OWLObject> listener) {
            listeners.add(listener);
            getProvider().addListener(listener);
        }

        @Override
        public void removeListener(OWLObjectHierarchyProviderListener<OWLObject> listener) {
            listeners.remove(listener);
            getProvider().removeListener(listener);
        }

        @Override
        public void dispose() {
            listeners.clear();
            getProvider().dispose();
        }
    }
}
