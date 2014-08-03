package org.protege.editor.owl.model.selection.axioms;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * User: nickdrummond
 * Date: May 22, 2008
 */
public class IndividualReferencingAxiomStrategy extends EntityReferencingAxiomsStrategy<OWLNamedIndividual> {

    @Override
    public String getName() {
        return "Axioms referencing a given individual (or individuals)";
    }

    @Override
    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLNamedIndividual individual : getEntities()){
            for (OWLOntology ont : ontologies){
                axioms.addAll(ont.getReferencingAxioms(individual,
                        Imports.EXCLUDED));
            }
        }
        return axioms;

    }

    @Override
    public Class<OWLNamedIndividual> getType() {
        return OWLNamedIndividual.class;
    }
}
