package org.protege.editor.owl.model.selection.axioms;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * User: nickdrummond
 * Date: May 21, 2008
 */
public class AllAxiomsStrategy extends AbstractAxiomSelectionStrategy {

    public String getName() {
        return "All axioms in the specified ontologies";
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLOntology ont : ontologies){
            axioms.addAll(ont.getAxioms());
        }
        return axioms;
    }
}
