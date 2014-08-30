package org.protege.editor.owl.ui.action;

import java.util.Set;

import javax.swing.JOptionPane;

import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.workspace.Workspace;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitor;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLEntityRemover;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class DeleteEntityAction extends SelectedOWLEntityAction {


    @Override
    protected void actionPerformed(OWLEntity selectedEntity) {
        boolean containsReferences = false;
        for (OWLOntology ont : getOWLModelManager().getOntologies()) {
            Set<OWLAxiom> referencingAxioms = ont.getReferencingAxioms(
                    selectedEntity, Imports.EXCLUDED);
            for (OWLAxiom ax : referencingAxioms) {
                if (!(ax instanceof OWLDeclarationAxiom)) {
                    containsReferences = true;
                    break;
                }
            }
        }
        if (containsReferences) {
            int ret = showUsageConfirmationDialog();
            if (ret == 1) {
                showUsage();
            }
            else if (ret == 0) {
                deleteEntity();
            }
        }
        else {
            int ret = showConfirmationDialog();
            if (ret == JOptionPane.YES_NO_OPTION) {
                deleteEntity();
            }
        }
    }


    private int showConfirmationDialog() {
        String rendering = getOWLModelManager().getRendering(getSelectedEntity());
        return JOptionPane.showConfirmDialog(getOWLWorkspace(),
                                             "Delete " + rendering + "?",
                                             "Really delete?",
                                             JOptionPane.YES_NO_OPTION,
                                             JOptionPane.WARNING_MESSAGE);
    }


    private int showUsageConfirmationDialog() {
        String rendering = getOWLModelManager().getRendering(getSelectedEntity());
        Object [] OPTIONS = {"Delete", "View usage", "Cancel"};
        return JOptionPane.showOptionDialog(getOWLWorkspace(),
                                            rendering + " is used throught the loaded ontologies.  Delete anyway?",
                                            "Entity is referenced!",
                                            JOptionPane.DEFAULT_OPTION,
                                            JOptionPane.WARNING_MESSAGE,
                                            null,
                                            OPTIONS,
                                            OPTIONS[1]);
    }


    private void deleteEntity() {
        OWLEntityRemover remover = new OWLEntityRemover(getOWLModelManager()
                .getOntologies());
        getOWLWorkspace().getOWLSelectionModel().getSelectedEntity().accept(remover);
        getOWLModelManager().applyChanges(remover.getChanges());
    }


    private void showUsage() {
        OWLEntity ent = getSelectedEntity();
        ent.accept(new OWLEntityVisitor() {
            @Override
            public void visit(OWLClass cls) {
                View view = getOWLWorkspace().showResultsView("OWLClassUsageView",
                                                              true,
                                                              Workspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }


            @Override
            public void visit(OWLDatatype dataType) {
            }


            @Override
            public void visit(OWLAnnotationProperty owlAnnotationProperty) {
                View view = getOWLWorkspace().showResultsView("OWLAnnotationPropertyUsageView",
                                                              true,
                                                              Workspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }


            @Override
            public void visit(OWLNamedIndividual individual) {
                View view = getOWLWorkspace().showResultsView("OWLIndividualUsageView",
                                                              true,
                                                              Workspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }


            @Override
            public void visit(OWLDataProperty property) {
                View view = getOWLWorkspace().showResultsView("OWLDataPropertyUsageView",
                                                              true,
                                                              Workspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }


            @Override
            public void visit(OWLObjectProperty property) {
                View view = getOWLWorkspace().showResultsView("OWLObjectPropertyUsageView",
                                                              true,
                                                              Workspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }
        });
    }


    @Override
    protected void disposeAction() throws Exception {
    }
}
