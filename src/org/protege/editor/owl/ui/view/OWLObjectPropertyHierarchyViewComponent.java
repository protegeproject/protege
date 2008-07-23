package org.protege.editor.owl.ui.view;

import org.protege.editor.core.ui.view.DisposableAction;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Jan-2007<br><br>
 */
public class OWLObjectPropertyHierarchyViewComponent extends AbstractOWLPropertyHierarchyViewComponent<OWLObjectProperty> {

    protected boolean isOWLObjectPropertyView() {
        return true;
    }


    protected void performExtraInitialisation() throws Exception {
        addAction(new AddPropertyAction(), "A", "A");
        addAction(new AddSubPropertyAction(), "A", "B");
        addAction(new DeleteObjectPropertyAction(getOWLEditorKit(), getTree()), "B", "A");

        getTree().setDragAndDropHandler(new OWLObjectPropertyTreeDropHandler(getOWLModelManager()));
    }


    protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
        return getOWLModelManager().getOWLObjectPropertyHierarchyProvider();
    }


    private void createProperty() {
        OWLEntityCreationSet<OWLObjectProperty> set = getOWLWorkspace().createOWLObjectProperty();
        if (set != null) {
            getOWLModelManager().applyChanges(set.getOntologyChanges());
            setSelectedEntity(set.getOWLEntity());
        }
    }


    private void createSubProperty() {
        OWLObjectProperty selProp = getSelectedProperty();
        if (selProp == null) {
            return;
        }
        OWLEntityCreationSet<OWLObjectProperty> set = getOWLWorkspace().createOWLObjectProperty();
        if (set != null) {
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.addAll(set.getOntologyChanges());
            OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
            OWLAxiom ax = df.getOWLSubObjectPropertyAxiom(set.getOWLEntity(), selProp);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLModelManager().applyChanges(changes);
            setSelectedEntity(set.getOWLEntity());
        }
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
