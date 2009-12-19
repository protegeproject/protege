package org.protege.editor.owl.model.repository.extractors;

import java.net.URI;

import org.apache.log4j.Logger;
import org.protege.xmlcatalog.owl.update.RdfExtractorConsumer;
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
            InputSource is = new InputSource(location.toURL().openStream());
            is.setSystemId(location.toURL().toString());
            parser.parse(is, consumer);
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
