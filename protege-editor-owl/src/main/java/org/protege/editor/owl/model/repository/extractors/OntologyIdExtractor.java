package org.protege.editor.owl.model.repository.extractors;

import java.net.URI;

import org.semanticweb.owlapi.model.OWLOntologyID;

import com.google.common.base.Optional;

public interface OntologyIdExtractor {
    
    Optional<OWLOntologyID> getOntologyId(URI location);
    
    
}
