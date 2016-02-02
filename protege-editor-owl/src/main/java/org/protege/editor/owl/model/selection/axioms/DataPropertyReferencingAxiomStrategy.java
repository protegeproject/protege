package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class DataPropertyReferencingAxiomStrategy extends EntityReferencingAxiomsStrategy<OWLDataProperty> {

    public String getName() {
        return "Axioms referencing a given data property (or properties)";
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLDataProperty p : getEntities()){
            for (OWLOntology ont : ontologies){
                axioms.addAll(ont.getReferencingAxioms(p));
            }
        }
        return axioms;
    }

    public Class<OWLDataProperty> getType() {
        return OWLDataProperty.class;
    }
}
