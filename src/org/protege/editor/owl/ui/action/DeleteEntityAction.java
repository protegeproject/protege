package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.view.View;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntityRemover;

import javax.swing.*;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class DeleteEntityAction extends SelectedOWLEntityAction {


    protected void actionPerformed(OWLEntity selectedEntity) {
        boolean containsReferences = false;
        for (OWLOntology ont : getOWLModelManager().getOntologies()) {
            Set<OWLAxiom> referencingAxioms = ont.getReferencingAxioms(selectedEntity);
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
        String rendering = getOWLModelManager().getOWLEntityRenderer().render(getSelectedEntity());
        return JOptionPane.showConfirmDialog(getOWLWorkspace(),
                                             "Delete " + rendering + "?",
                                             "Really delete?",
                                             JOptionPane.YES_NO_OPTION,
                                             JOptionPane.WARNING_MESSAGE);
    }


    private int showUsageConfirmationDialog() {
        String rendering = getOWLModelManager().getOWLEntityRenderer().render(getSelectedEntity());
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
        OWLEntityRemover remover = new OWLEntityRemover(getOWLModelManager().getOWLOntologyManager(),
                                                        getOWLModelManager().getOntologies());
        getOWLWorkspace().getOWLSelectionModel().getSelectedEntity().accept(remover);
        getOWLModelManager().applyChanges(remover.getChanges());
    }


    private void showUsage() {
        OWLEntity ent = getSelectedEntity();
        ent.accept(new OWLEntityVisitor() {
            public void visit(OWLClass cls) {
                View view = getOWLWorkspace().showResultsView("OWLClassUsageView",
                                                              true,
                                                              OWLWorkspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }


            public void visit(OWLDataType dataType) {
            }


            public void visit(OWLIndividual individual) {
                View view = getOWLWorkspace().showResultsView("OWLIndividualUsageView",
                                                              true,
                                                              OWLWorkspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }


            public void visit(OWLDataProperty property) {
                View view = getOWLWorkspace().showResultsView("OWLDataPropertyUsageView",
                                                              true,
                                                              OWLWorkspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }


            public void visit(OWLObjectProperty property) {
                View view = getOWLWorkspace().showResultsView("OWLObjectPropertyUsageView",
                                                              true,
                                                              OWLWorkspace.BOTTOM_RESULTS_VIEW);
                view.setPinned(true);
            }
        });
    }


    protected void disposeAction() throws Exception {
    }
}
