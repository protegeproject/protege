package org.protege.editor.owl.model.repository.extractors;

import java.net.URI;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class LastResortExtractor implements OntologyIdExtractor {
    private Logger log = Logger.getLogger(LastResortExtractor.class);
    private URI location;

    public OWLOntologyID getOntologyId() {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(location));
            return ontology.getOntologyID();
        }
        catch (Throwable t) {
            log.info("Exception caught trying to get ontology id for " + location, t);
            return null;
        }
    }

    public void setPhysicalAddress(URI location) {
        this.location = location;
    }

}
