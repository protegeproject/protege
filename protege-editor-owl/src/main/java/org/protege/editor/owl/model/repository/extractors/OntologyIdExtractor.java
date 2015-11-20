package org.protege.editor.owl.model.repository.extractors;

import com.google.common.base.Optional;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.net.URI;

public interface OntologyIdExtractor {
    
    Optional<OWLOntologyID> getOntologyId(URI location);
    
    
}
