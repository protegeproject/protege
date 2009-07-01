package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ui.view.View;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.*;


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
                showResultsView("org.protege.editor.owl.OWLClassUsageView");
            }

            public void visit(OWLDataProperty property) {
                showResultsView("org.protege.editor.owl.OWLDataPropertyUsageView");
            }

            public void visit(OWLObjectProperty property) {
                showResultsView("org.protege.editor.owl.OWLObjectPropertyUsageView");
            }

            public void visit(OWLAnnotationProperty owlAnnotationProperty) {
                showResultsView("org.protege.editor.owl.OWLAnnotationPropertyUsageView");
            }

            public void visit(OWLNamedIndividual individual) {
                showResultsView("org.protege.editor.owl.OWLIndividualUsageView");
            }

            public void visit(OWLDatatype dataType) {
            }

            private void showResultsView(String viewId) {
                View view = getOWLWorkspace().showResultsView(viewId,
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
