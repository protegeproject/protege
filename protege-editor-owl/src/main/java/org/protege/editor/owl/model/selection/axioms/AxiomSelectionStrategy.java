package org.protege.editor.owl.model.selection.axioms;

import java.beans.PropertyChangeListener;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public interface AxiomSelectionStrategy {

    String getName();

    Set<? extends OWLAxiom> getAxioms(Set<OWLOntology> ontologies);

//    void setOntologies(Set<OWLOntology> ontologies);
//
//    Set<OWLOntology> getOntologies();

    void addPropertyChangeListener(PropertyChangeListener l);

    void removePropertyChangeListener(PropertyChangeListener l);    
}
