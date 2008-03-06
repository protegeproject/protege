package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.tree.OWLModelManagerTree;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLDataPropertyHierarchyViewComponent extends AbstractOWLDataPropertyViewComponent {

    private OWLModelManagerTree<OWLDataProperty> tree;


    public void initialiseView() throws Exception {
        tree = new OWLModelManagerTree<OWLDataProperty>(getOWLEditorKit(),
                                                        getOWLModelManager().getOWLDataPropertyHierarchyProvider());
        tree.setDragAndDropHandler(new OWLDataPropertyTreeDropHandler(getOWLModelManager()));
        setLayout(new BorderLayout());
        add(new JScrollPane(tree), BorderLayout.CENTER);
        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                setSelectedEntity(tree.getSelectedOWLObject());
            }
        });
        tree.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setSelectedEntity(tree.getSelectedOWLObject());
            }
        });
        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setSelectedEntity(tree.getSelectedOWLObject());
            }
        });

        addAction(new AddPropertyAction(), "A", "A");
        addAction(new AddSubPropertyAction(), "A", "B");
        addAction(new DeleteDataPropertyAction(getOWLEditorKit(), tree), "B", "A");
    }


    public OWLDataProperty getSelectedDataProperty() {
        return tree.getSelectedOWLObject();
    }


    protected OWLDataProperty updateView(OWLDataProperty property) {
        tree.setSelectedOWLObject(property);
        return property;
    }


    public void disposeView() {
        tree.dispose();
    }


    public void show(OWLDataProperty property) {
        tree.setSelectedOWLObject(property);
    }


    private void createProperty() {
        OWLEntityCreationSet<OWLDataProperty> set = getOWLWorkspace().createOWLDataProperty();
        if (set != null) {
            getOWLModelManager().applyChanges(set.getOntologyChanges());
            tree.setSelectedOWLObject(set.getOWLEntity());
        }
    }


    public Set<OWLDataProperty> getSelectedProperties() {
        return new HashSet<OWLDataProperty>(tree.getSelectedOWLObjects());
    }

    
    private void createSubProperty() {
        if (tree.getSelectedOWLObject() == null) {
            return;
        }
        OWLEntityCreationSet<OWLDataProperty> set = getOWLWorkspace().createOWLDataProperty();
        if (set != null) {
            java.util.List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(set.getOntologyChanges());
            OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
            OWLDataProperty selProp = tree.getSelectedOWLObject();
            OWLAxiom ax = df.getOWLSubDataPropertyAxiom(set.getOWLEntity(), selProp);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLModelManager().applyChanges(changes);
            tree.setSelectedOWLObject(set.getOWLEntity());
        }
    }


    private class AddPropertyAction extends DisposableAction {

        public AddPropertyAction() {
            super("Add property", OWLIcons.getIcon("property.data.add.png"));
        }


        public void dispose() {
        }


        public void actionPerformed(ActionEvent e) {
            createProperty();
        }
    }


    private class AddSubPropertyAction extends DisposableAction {

        public AddSubPropertyAction() {
            super("Add sub property", OWLIcons.getIcon("property.data.addsub.png"));
        }


        public void dispose() {
        }


        public void actionPerformed(ActionEvent e) {
            createSubProperty();
        }
    }
}
