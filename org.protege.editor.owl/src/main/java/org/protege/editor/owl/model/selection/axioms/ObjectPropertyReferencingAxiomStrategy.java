package org.protege.editor.owl.model.selection.axioms;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class ObjectPropertyReferencingAxiomStrategy extends EntityReferencingAxiomsStrategy<OWLObjectProperty> {

    @Override
    public String getName() {
        return "Axioms referencing a given object property (or properties)";
    }

    @Override
    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLObjectProperty p : getEntities()){
            for (OWLOntology ont : ontologies){
                axioms.addAll(ont.getReferencingAxioms(p, Imports.EXCLUDED));
            }
        }
        return axioms;
    }

    @Override
    public Class<OWLObjectProperty> getType() {
        return OWLObjectProperty.class;
    }
}
