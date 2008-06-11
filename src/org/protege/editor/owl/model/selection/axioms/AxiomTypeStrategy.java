package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 21, 2008
 */
public class AxiomTypeStrategy extends AbstractAxiomSelectionStrategy {

    private Set<AxiomType<? extends OWLAxiom>> types = new HashSet<AxiomType<? extends OWLAxiom>>();

    public static final String CHANGED_AXIOM_TYPE = "change.axiomtype";


    public String getName() {
        return "Axioms by type";
    }

    public void setTypes(Set<AxiomType<? extends OWLAxiom>> types){
        this.types = types;
        notifyPropertyChange(CHANGED_AXIOM_TYPE);
    }

    public Set<OWLAxiom> getAxioms() {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        for (OWLOntology ont : getOntologies()){
            for (AxiomType<? extends OWLAxiom> type : types){
                axioms.addAll(ont.getAxioms(type));
            }
        }
        return axioms;
    }
}
