package org.protege.editor.owl.model.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.model.repository.extractors.LastResortExtractor;
import org.protege.editor.owl.model.repository.extractors.OntologyIdExtractor;
import org.semanticweb.owlapi.model.OWLOntologyID;

public class MasterOntologyIDExtractor implements OntologyIdExtractor {
    
    private List<OntologyIdExtractor> extractors = new ArrayList<OntologyIdExtractor>();
    
    public MasterOntologyIDExtractor() {
        extractors.add(new LastResortExtractor());
    }

    public OWLOntologyID getOntologyId() {
        OWLOntologyID id = null;
        for (OntologyIdExtractor extractor : extractors) {
            if ((id = extractor.getOntologyId()) != null) {
                break;
            }
        }
        return id;
    }

    public void setPhysicalAddress(URI location) {
        for (OntologyIdExtractor extractor : extractors) {
            extractor.setPhysicalAddress(location);
        }
    }

}
