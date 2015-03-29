package org.protege.editor.owl.ui.action;

import java.util.List;

import org.protege.editor.owl.ui.rename.RenameEntityPanel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.util.OWLEntityRenamer;


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
                ensureSelected(getOWLDataFactory().getOWLAnnotationProperty(iri));
            }

            public void visit(OWLNamedIndividual individual) {
                ensureSelected(getOWLDataFactory().getOWLNamedIndividual(iri));
            }

            public void visit(OWLDatatype dataType) {
                ensureSelected(getOWLDataFactory().getOWLDatatype(iri));
            }
        });
    }


    private void ensureSelected(OWLEntity entity) {
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(entity);
    }


    protected void disposeAction() throws Exception {
    }
}
