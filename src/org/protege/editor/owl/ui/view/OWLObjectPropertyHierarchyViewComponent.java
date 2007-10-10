package org.protege.editor.owl.ui.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.action.OWLObjectHierarchyDeleter;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.util.OWLEntitySetProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLObjectPropertyHierarchyViewComponent extends AbstractOWLObjectPropertyViewComponent implements Findable<OWLObjectProperty>, Deleteable {

    private OWLModelManagerTree<OWLObjectProperty> tree;


    public void initialiseView() throws Exception {
        tree = new OWLModelManagerTree<OWLObjectProperty>(getOWLEditorKit(),
                                                          getOWLModelManager().getOWLObjectPropertyHierarchyProvider());
        tree.setDragAndDropHandler(new OWLObjectPropertyTreeDropHandler(getOWLModelManager()));
        setLayout(new BorderLayout());
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                transmitSelection();
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                transmitSelection();
            }
        });
        addAction(new AddPropertyAction(), "A", "A");
        addAction(new AddSubPropertyAction(), "A", "B");
        addAction(new DeleteObjectPropertyAction(getOWLEditorKit(), tree), "B", "A");
    }


    private void transmitSelection() {
        OWLObjectProperty prop = tree.getSelectedOWLObject();
        setSelectedEntity(prop);
        mediator.fireStateChanged(this);
    }


    protected OWLObjectProperty updateView(OWLObjectProperty property) {
        OWLObjectProperty treeProp = tree.getSelectedOWLObject();
        if (treeProp != null && property != null) {
            if (treeProp.equals(property)) {
                return property;
            }
        }
        tree.setSelectedOWLObject(property);
        return property;
    }


    public OWLObjectProperty getSelectedProperty() {
        return tree.getSelectedOWLObject();
    }


    public void disposeView() {
        tree.dispose();
    }


    private void createProperty() {
        OWLEntityCreationSet<OWLObjectProperty> set = getOWLWorkspace().createOWLObjectProperty();
        if (set != null) {
            getOWLModelManager().applyChanges(set.getOntologyChanges());
            tree.setSelectedOWLObject(set.getOWLEntity());
        }
    }


    private void createSubProperty() {
        if (tree.getSelectedOWLObject() == null) {
            return;
        }
        OWLEntityCreationSet<OWLObjectProperty> set = getOWLWorkspace().createOWLObjectProperty();
        if (set != null) {
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(set.getOntologyChanges());
            OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
            OWLObjectProperty selProp = tree.getSelectedOWLObject();
            OWLAxiom ax = df.getOWLSubObjectPropertyAxiom(set.getOWLEntity(), selProp);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLModelManager().applyChanges(changes);
            tree.setSelectedOWLObject(set.getOWLEntity());
        }
    }


    public Set<OWLObjectProperty> getSelectedProperties() {
        return new HashSet<OWLObjectProperty>(tree.getSelectedOWLObjects());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Findable
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public List<OWLObjectProperty> find(String match) {
        return getOWLModelManager().getMatchingOWLObjectProperties(match);
    }


    public void show(OWLObjectProperty owlEntity) {
        tree.setSelectedOWLObject(owlEntity);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Deletable
    //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ChangeListenerMediator mediator = new ChangeListenerMediator();


    public boolean canDelete() {
        return !tree.getSelectedOWLObjects().isEmpty();
    }


    public void handleDelete() {
        OWLObjectHierarchyDeleter<OWLObjectProperty> deleter = new OWLObjectHierarchyDeleter<OWLObjectProperty>(
                getOWLEditorKit(),
                getOWLModelManager().getOWLObjectPropertyHierarchyProvider(),
                new OWLEntitySetProvider<OWLObjectProperty>() {
                    public Set<OWLObjectProperty> getEntities() {
                        return new HashSet<OWLObjectProperty>(tree.getSelectedOWLObjects());
                    }
                },
                "properties");

        deleter.performDeletion();
    }


    public void addChangeListener(ChangeListener listener) {
        mediator.addChangeListener(listener);
    }


    public void removeChangeListener(ChangeListener listener) {
        mediator.removeChangeListener(listener);
    }


    public boolean canPerformAction() {
        return !tree.getSelectedOWLObjects().isEmpty();
    }


    private class AddPropertyAction extends DisposableAction {

        public AddPropertyAction() {
            super("Add property", OWLIcons.getIcon("property.object.add.png"));
        }


        public void dispose() {
        }


        public void actionPerformed(ActionEvent e) {
            createProperty();
        }
    }


    private class AddSubPropertyAction extends DisposableAction {

        public AddSubPropertyAction() {
            super("Add sub property", OWLIcons.getIcon("property.object.addsub.png"));
        }


        public void dispose() {
        }


        public void actionPerformed(ActionEvent e) {
            createSubProperty();
        }
    }
}
