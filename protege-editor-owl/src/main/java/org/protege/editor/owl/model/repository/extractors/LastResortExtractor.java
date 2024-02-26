package org.protege.editor.owl.model.repository.extractors;

import java.net.URI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class LastResortExtractor implements OntologyIdExtractor {

    private Logger log = LoggerFactory.getLogger(LastResortExtractor.class);

    public Optional<OWLOntologyID> getOntologyId(URI location) {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create(location));
            return Optional.of(ontology.getOntologyID());
        }
        catch (Throwable t) {
            log.info("Exception caught trying to get ontology id for " + location, t);
            return null;
        }
    }
}
