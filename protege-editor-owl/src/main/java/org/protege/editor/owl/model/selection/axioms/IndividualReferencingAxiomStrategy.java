package org.protege.editor.owl.model.selection.axioms;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * User: nickdrummond
 * Date: May 22, 2008
 */
public class IndividualReferencingAxiomStrategy extends EntityReferencingAxiomsStrategy<OWLNamedIndividual> {

    public String getName() {
        return "Axioms referencing a given individual (or individuals)";
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLNamedIndividual individual : getEntities()){
            for (OWLOntology ont : ontologies){
                axioms.addAll(ont.getReferencingAxioms(individual));
            }
        }
        return axioms;

    }

    public Class<OWLNamedIndividual> getType() {
        return OWLNamedIndividual.class;
    }
}
