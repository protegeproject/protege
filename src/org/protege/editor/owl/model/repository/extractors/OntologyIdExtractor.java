package org.protege.editor.owl.model.repository.extractors;

import java.net.URI;
import org.semanticweb.owlapi.model.OWLOntologyID;

public interface OntologyIdExtractor {
    
    void setPhysicalAddress(URI location);
    
    OWLOntologyID getOntologyId();
    
    
}
