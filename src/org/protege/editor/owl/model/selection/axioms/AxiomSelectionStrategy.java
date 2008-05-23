package org.protege.editor.owl.model.selection.axioms;

import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Set;

/**
 * User: nickdrummond
 * Date: May 20, 2008
 */
public interface AxiomSelectionStrategy {

    String getName();

    Set<OWLAxiom> getAxioms(Set<OWLOntology> onts);
}
