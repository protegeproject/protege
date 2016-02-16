package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 21, 2008
 */
public class AxiomTypeStrategy extends AbstractAxiomSelectionStrategy {

    private Set<AxiomType<? extends OWLAxiom>> types = new HashSet<>();

    public static final String CHANGED_AXIOM_TYPE = "change.axiomtype";


    public String getName() {
        return "Axioms by type";
    }

    public void setTypes(Set<AxiomType<? extends OWLAxiom>> types){
        this.types = types;
        notifyPropertyChange(CHANGED_AXIOM_TYPE);
    }

    public Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies) {
        Set<OWLAxiom> axioms = new HashSet<>();
        for (OWLOntology ont : ontologies){
            for (AxiomType<? extends OWLAxiom> type : types){
                axioms.addAll(ont.getAxioms(type));
            }
        }
        return axioms;
    }
}
