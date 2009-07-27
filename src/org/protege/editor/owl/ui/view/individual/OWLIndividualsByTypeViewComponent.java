package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.core.ui.RefreshableComponent;
import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.hierarchy.IndividualsByTypeHierarchyProvider;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.DeleteIndividualAction;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.protege.editor.owl.ui.tree.OWLObjectTree;
import org.protege.editor.owl.ui.tree.OWLObjectTreeCellRenderer;
import org.protege.editor.owl.ui.tree.OWLTreeDragAndDropHandler;
import org.protege.editor.owl.ui.view.AbstractOWLSelectionViewComponent;
import org.protege.editor.owl.ui.view.ChangeListenerMediator;
import org.protege.editor.owl.ui.view.CreateNewTarget;
import org.protege.editor.owl.ui.view.Findable;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-May-2007<br><br>
 */
public class OWLIndividualsByTypeViewComponent extends AbstractOWLSelectionViewComponent
        implements Findable<OWLNamedIndividual>, CreateNewTarget, RefreshableComponent {

    private OWLObjectHierarchyProvider<OWLObject> provider;

    private OWLObjectTree<OWLObject> tree;

    private ChangeListenerMediator changeListenerMediator;

    private Set<EventType> updateEvents = new HashSet<EventType>();

    private TreeSelectionListener listener = new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
            transmitSelection();
        }
    };

    private OWLModelManagerListener managerListener = new OWLModelManagerListener(){
        public void handleChange(OWLModelManagerChangeEvent event) {
            if (updateEvents.contains(event.getType())){
                provider.setOntologies(getOWLModelManager().getActiveOntologies()); // forces refresh
            }
        }
    };


    public void initialiseView() throws Exception {
        setLayout(new BorderLayout());

        updateEvents.add(EventType.ACTIVE_ONTOLOGY_CHANGED);
        updateEvents.add(EventType.ONTOLOGY_LOADED);
        updateEvents.add(EventType.ONTOLOGY_VISIBILITY_CHANGED);
        updateEvents.add(EventType.ENTITY_RENDERER_CHANGED);

        getOWLModelManager().addListener(managerListener);

        provider = new IndividualsByTypeHierarchyProvider(getOWLModelManager().getOWLOntologyManager());

        tree = new OWLModelManagerTree<OWLObject>(getOWLEditorKit(), provider);
        tree.setOWLObjectComparator(getOWLModelManager().getOWLObjectComparator());
        tree.setCellRenderer(new OWLObjectTreeCellRenderer(getOWLEditorKit()){
            protected String getRendering(Object object) {
                StringBuilder label = new StringBuilder(super.getRendering(object));
                if (object instanceof OWLClass){
                    int size = provider.getChildren((OWLClass)object).size();
                    label.append(" (");
                    label.append(size);
                    label.append(")");
                }
                return label.toString();
            }
        });

        add(new JScrollPane(tree));

        provider.setOntologies(getOWLModelManager().getActiveOntologies());

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
    }


    private void handleAdd(OWLObject child, OWLObject toParent) {
        if (child instanceof OWLNamedIndividual){
            OWLNamedIndividual ind = (OWLNamedIndividual)child;

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

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

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

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
        addAction(new DisposableAction("Add individual", OWLIcons.getIcon("individual.add.png")) {

            public void actionPerformed(ActionEvent e) {
                createNewObject();
            }

            public void dispose() {
            }
        } , "A", "A");
        addAction(new DeleteIndividualAction(getOWLEditorKit(),
                                             new OWLEntitySetProvider<OWLNamedIndividual>() {
                                                 public Set<OWLNamedIndividual> getEntities() {
                                                     return getSelectedIndividuals();
                                                 }
                                             }), "B", "A");
    }


    public void disposeView() {
        tree.dispose();
        getOWLModelManager().removeListener(managerListener);
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
        Set<OWLNamedIndividual> selIndivs = new HashSet<OWLNamedIndividual>();
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
                getOWLWorkspace().getOWLSelectionModel().setSelectedEntity((OWLEntity) obj);
            }
        }
        changeListenerMediator.fireStateChanged(this);
    }


    //////// Findable

    public List<OWLNamedIndividual> find(String match) {
        return new ArrayList<OWLNamedIndividual>(getOWLModelManager().getEntityFinder().getMatchingOWLIndividuals(match));
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
        java.util.List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        changes.addAll(set.getOntologyChanges());
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
}
