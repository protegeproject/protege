package org.protege.editor.owl.model.selection.axioms;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class ClassReferencingAxiomsStrategy extends EntityReferencingAxiomsStrategy<OWLClass> {

    @Override
    public String getName() {
        return "Axioms referencing a given class (or classes)";
    }

    @Override
    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLClass cls : getEntities()){
            for (OWLOntology ont : ontologies){
                axioms.addAll(ont.getReferencingAxioms(cls, Imports.EXCLUDED));
            }
        }
        return axioms;
    }

    @Override
    public Class<OWLClass> getType() {
        return OWLClass.class;
    }
}
