package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.rename.RenameEntityPanel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

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
        final IRI iri = RenameEntityPanel.showDialog(getOWLEditorKit(), selectedEntity);
        if (iri == null) {
            return;
        }
        final List<OWLOntologyChange> changes;
        if (RenameEntityPanel.isAutoRenamePuns()){
            changes = owlEntityRenamer.changeIRI(selectedEntity.getIRI(), iri);
        }
        else{
            changes = owlEntityRenamer.changeIRI(selectedEntity, iri);
        }
        getOWLModelManager().applyChanges(changes);

        selectedEntity.accept(new OWLEntityVisitor() {
            public void visit(OWLClass cls) {
                ensureSelected(getOWLDataFactory().getOWLClass(iri));
            }

            public void visit(OWLObjectProperty property) {
                ensureSelected(getOWLDataFactory().getOWLObjectProperty(iri));
            }

            public void visit(OWLDataProperty property) {
                ensureSelected(getOWLDataFactory().getOWLDataProperty(iri));
            }

            public void visit(OWLAnnotationProperty owlAnnotationProperty) {
                ensureSelected(getOWLDataFactory().getOWLDataProperty(iri));
            }

            public void visit(OWLNamedIndividual individual) {
                ensureSelected(getOWLDataFactory().getOWLNamedIndividual(iri));
            }

            public void visit(OWLDatatype dataType) {
            }
        });
    }


    private void ensureSelected(OWLEntity entity) {
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(entity);
    }


    protected void disposeAction() throws Exception {
    }
}
