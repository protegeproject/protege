package org.protege.editor.owl.model.axiom;

import org.protege.editor.owl.model.HasActiveOntology;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class ActiveOntologyLocationStrategy implements FreshAxiomLocationStrategy {

    @Override
    public OWLOntology getFreshAxiomLocation(OWLAxiom axiom, HasActiveOntology hasActiveOntology) {
        return hasActiveOntology.getActiveOntology();
    }
}
