package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLDataPropertyHierarchyViewComponent extends AbstractOWLPropertyHierarchyViewComponent<OWLDataProperty> {

    protected boolean isOWLDataPropertyView() {
        return true;
    }


    protected void performExtraInitialisation() throws Exception {
        addAction(new AddPropertyAction(), "A", "A");
        addAction(new AddSubPropertyAction(), "A", "B");
        addAction(new DeleteDataPropertyAction(getOWLEditorKit(), getTree()), "B", "A");

        getTree().setDragAndDropHandler(new OWLDataPropertyTreeDropHandler(getOWLModelManager()));
    }


    protected OWLObjectHierarchyProvider<OWLDataProperty> getHierarchyProvider() {
        return getOWLModelManager().getOWLDataPropertyHierarchyProvider();
    }


    private void createProperty() {
        OWLEntityCreationSet<OWLDataProperty> set = getOWLWorkspace().createOWLDataProperty();
        if (set != null) {
            getOWLModelManager().applyChanges(set.getOntologyChanges());
            show(set.getOWLEntity());
        }
    }


    private void createSubProperty() {
        OWLDataProperty selProp = getSelectedProperty();
        if (selProp == null) {
            return;
        }
        OWLEntityCreationSet<OWLDataProperty> set = getOWLWorkspace().createOWLDataProperty();
        if (set != null) {
            java.util.List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(set.getOntologyChanges());
            OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
            OWLAxiom ax = df.getOWLSubDataPropertyAxiom(set.getOWLEntity(), selProp);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLModelManager().applyChanges(changes);
            show(set.getOWLEntity());
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
