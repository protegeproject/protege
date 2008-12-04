package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.rename.RenameEntityPanel;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntityRenamer;

import java.net.URI;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class RenameEntityAction extends SelectedOWLEntityAction {


    protected void actionPerformed(OWLEntity selectedEntity) {
        OWLEntityRenamer owlEntityRenamer = new OWLEntityRenamer(getOWLModelManager().getOWLOntologyManager(),
                                                                 getOWLModelManager().getOntologies());
        final URI uri = RenameEntityPanel.showDialog(getOWLEditorKit(), selectedEntity);
        if (uri == null) {
            return;
        }
        final List<OWLOntologyChange> changes;
        if (RenameEntityPanel.isAutoRenamePuns()){
            changes = owlEntityRenamer.changeURI(selectedEntity.getURI(), uri);
        }
        else{
            changes = owlEntityRenamer.changeURI(selectedEntity, uri);
        }
        getOWLModelManager().applyChanges(changes);

        selectedEntity.accept(new OWLEntityVisitor() {
            public void visit(OWLClass cls) {
                ensureSelected(getOWLDataFactory().getOWLClass(uri));
            }


            public void visit(OWLObjectProperty property) {
                ensureSelected(getOWLDataFactory().getOWLObjectProperty(uri));
            }


            public void visit(OWLDataProperty property) {
                ensureSelected(getOWLDataFactory().getOWLDataProperty(uri));
            }


            public void visit(OWLIndividual individual) {
                ensureSelected(getOWLDataFactory().getOWLIndividual(uri));
            }


            public void visit(OWLDataType dataType) {
            }
        });
    }


    private void ensureSelected(OWLEntity entity) {
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(entity);
    }


    protected void disposeAction() throws Exception {
    }
}
