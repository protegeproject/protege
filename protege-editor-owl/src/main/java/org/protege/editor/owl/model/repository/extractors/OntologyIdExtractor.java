package org.protege.editor.owl.model.repository.extractors;

import org.semanticweb.owlapi.model.OWLOntologyID;

import java.net.URI;
import java.util.Optional;

public interface OntologyIdExtractor {
    
    Optional<OWLOntologyID> getOntologyId(URI location);
}
