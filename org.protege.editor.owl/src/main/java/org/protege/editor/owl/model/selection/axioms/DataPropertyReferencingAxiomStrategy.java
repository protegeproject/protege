package org.protege.editor.owl.model.selection.axioms;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class DataPropertyReferencingAxiomStrategy extends EntityReferencingAxiomsStrategy<OWLDataProperty> {

    @Override
    public String getName() {
        return "Axioms referencing a given data property (or properties)";
    }

    @Override
    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLDataProperty p : getEntities()){
            for (OWLOntology ont : ontologies){
                axioms.addAll(ont.getReferencingAxioms(p, Imports.EXCLUDED));
            }
        }
        return axioms;
    }

    @Override
    public Class<OWLDataProperty> getType() {
        return OWLDataProperty.class;
    }
}
