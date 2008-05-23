package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 21, 2008
 */
public class AllAxiomsStrategy implements AxiomSelectionStrategy {

    public String getName() {
        return "All axioms in the specified ontologies";
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> onts) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ont : onts){
            axioms.addAll(ont.getAxioms());
        }
        return axioms;
    }
}
