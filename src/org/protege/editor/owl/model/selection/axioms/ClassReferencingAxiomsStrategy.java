package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class ClassReferencingAxiomsStrategy extends EntityReferencingAxiomsStrategy<OWLClass> {

    public String getName() {
        return "Axioms referencing a given class (or classes)";
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLClass cls : getEntities()){
            for (OWLOntology ont : ontologies){
                axioms.addAll(ont.getReferencingAxioms(cls));
            }
        }
        return axioms;
    }

    public Class<OWLClass> getType() {
        return OWLClass.class;
    }
}
