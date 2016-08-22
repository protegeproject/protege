package org.protege.editor.owl.model;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Jul 16
 */
public class ChangeListMinimizer {

    public List<OWLOntologyChange> getMinimisedChanges(List<? extends OWLOntologyChange> changes) {

        final Set<OWLAxiom> axiomsToAdd = new HashSet<>();
        final Set<OWLAxiom> axiomsToRemove = new HashSet<>();

        for (OWLOntologyChange change : changes) {
            if (change.isAddAxiom()) {
                if(!axiomsToRemove.remove(change.getAxiom())) {
                    axiomsToAdd.add(change.getAxiom());
                }
            }
            else if (change.isRemoveAxiom()) {
                if (!axiomsToAdd.remove(change.getAxiom())) {
                    axiomsToRemove.add(change.getAxiom());
                }

            }
        }

        // Minimise changes
        final List<OWLOntologyChange> minimisedChanges = new ArrayList<>();
        for (OWLOntologyChange change : changes) {
            if (change.isAddAxiom()) {
                if (axiomsToAdd.contains(change.getAxiom())) {
                    minimisedChanges.add(change);
                    axiomsToAdd.remove(change.getAxiom());
                }
            }
            else if (change.isRemoveAxiom()) {
                if (axiomsToRemove.contains(change.getAxiom())) {
                    minimisedChanges.add(change);
                    axiomsToRemove.remove(change.getAxiom());
                }
            }
            else {
                minimisedChanges.add(change);
            }
        }
        return minimisedChanges;
    }

}
