package org.protege.editor.owl.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Jul 16
 */
public class ChangeListMinimizer {

    public List<OWLOntologyChange> getMinimisedChanges(List<? extends OWLOntologyChange> changes) {

        final Multimap<OWLOntology, OWLAxiom> axiomsToAdd = HashMultimap.create();
        final Multimap<OWLOntology, OWLAxiom> axiomsToRemove = HashMultimap.create();

        for (OWLOntologyChange change : changes) {
            if (change.isAddAxiom()) {
                if(!axiomsToRemove.get(change.getOntology()).remove(change.getAxiom())) {
                    axiomsToAdd.get(change.getOntology()).add(change.getAxiom());
                }
            }
            else if (change.isRemoveAxiom()) {
                if (!axiomsToAdd.get(change.getOntology()).remove(change.getAxiom())) {
                    axiomsToRemove.get(change.getOntology()).add(change.getAxiom());
                }

            }
        }

        // Minimise changes
        final List<OWLOntologyChange> minimisedChanges = new ArrayList<>();
        for (OWLOntologyChange change : changes) {
            if (change.isAddAxiom()) {
                if (axiomsToAdd.get(change.getOntology()).contains(change.getAxiom())) {
                    minimisedChanges.add(change);
                    axiomsToAdd.get(change.getOntology()).remove(change.getAxiom());
                }
            }
            else if (change.isRemoveAxiom()) {
                if (axiomsToRemove.get(change.getOntology()).contains(change.getAxiom())) {
                    minimisedChanges.add(change);
                    axiomsToRemove.get(change.getOntology()).remove(change.getAxiom());
                }
            }
            else {
                minimisedChanges.add(change);
            }
        }
        return minimisedChanges;
    }

}
