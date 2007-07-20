package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.view.View;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class ShowUsageAction extends SelectedOWLEntityAction {


    protected void actionPerformed(OWLEntity selectedEntity) {
        OWLEntity ent = getSelectedEntity();
        if (ent == null) {
            return;
        }
        ent.accept(new OWLEntityVisitor() {
            public void visit(OWLClass cls) {
                View view = getOWLWorkspace().showResultsView("org.protege.editor.owl.OWLClassUsageView",
                                                              true,
                                                              OWLWorkspace.BOTTOM_RESULTS_VIEW);
                if (view != null) {
                    view.setPinned(true);
                }
            }


            public void visit(OWLDataType dataType) {
            }


            public void visit(OWLIndividual individual) {
                View view = getOWLWorkspace().showResultsView("org.protege.editor.owl.OWLIndividualUsageView",
                                                              true,
                                                              OWLWorkspace.BOTTOM_RESULTS_VIEW);
                if (view != null) {
                    view.setPinned(true);
                }
            }


            public void visit(OWLDataProperty property) {
                View view = getOWLWorkspace().showResultsView("org.protege.editor.owl.OWLDataPropertyUsageView",
                                                              true,
                                                              OWLWorkspace.BOTTOM_RESULTS_VIEW);
                if (view != null) {
                    view.setPinned(true);
                }
            }


            public void visit(OWLObjectProperty property) {
                View view = getOWLWorkspace().showResultsView("org.protege.editor.owl.OWLObjectPropertyUsageView",
                                                              true,
                                                              OWLWorkspace.BOTTOM_RESULTS_VIEW);
                if (view != null) {
                    view.setPinned(true);
                }
            }
        });
    }


    protected void disposeAction() throws Exception {
    }
}
