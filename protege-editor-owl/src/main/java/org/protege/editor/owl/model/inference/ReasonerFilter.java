package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.model.OWLOntology;

public interface ReasonerFilter {

    OWLOntology getFilteredOntology(OWLOntology ontology);
}
