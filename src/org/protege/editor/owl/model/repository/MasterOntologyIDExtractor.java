package org.protege.editor.owl.model.repository;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.model.repository.extractors.LastResortExtractor;
import org.protege.editor.owl.model.repository.extractors.OntologyIdExtractor;
import org.protege.editor.owl.model.repository.extractors.RdfXmlExtractor;
import org.semanticweb.owlapi.model.OWLOntologyID;

public class MasterOntologyIDExtractor implements OntologyIdExtractor {
    
    private List<OntologyIdExtractor> extractors = new ArrayList<OntologyIdExtractor>();
    
    // TODO - all the callers of this method have problems which need fixing.
    public MasterOntologyIDExtractor() { 
        extractors.add(new RdfXmlExtractor());
        extractors.add(new LastResortExtractor());
    }
    
    public MasterOntologyIDExtractor(URI location) {
        this();
        setPhysicalAddress(location);
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
