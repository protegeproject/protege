package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public class DataPropertyReferencingAxiomStrategy extends EntityReferencingAxiomsStrategy<OWLDataProperty> {

    public String getName() {
        return "Axioms referencing a given data property";
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> onts) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLDataProperty p : getEntities()){
            for (OWLOntology ont : onts){
                axioms.addAll(ont.getReferencingAxioms(p));
            }
        }
        return axioms;
    }

    public Class<OWLDataProperty> getType() {
        return OWLDataProperty.class;
    }
}
