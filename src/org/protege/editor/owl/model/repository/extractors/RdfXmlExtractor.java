package org.protege.editor.owl.model.repository.extractors;

import java.net.URI;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.syntax.RDFParser;
import org.xml.sax.InputSource;

public class RdfXmlExtractor implements OntologyIdExtractor {
    private Logger log = Logger.getLogger(RdfXmlExtractor.class);
    private URI location;

    public OWLOntologyID getOntologyId() {
        RdfExtractorConsumer consumer = new RdfExtractorConsumer();
        RDFParser parser = new RDFParser();
        try {
            parser.parse(new InputSource(location.toURL().openStream()), consumer);
        }
        catch (StopParseEarlyException e) {
            ;
        }
        catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Exception caught trying to extract ontology from rdf file at  " + location, t);
            }
            return null;
        }
        return consumer.getOntologyID();
    }

    public void setPhysicalAddress(URI location) {
        this.location = location;
    }

}
