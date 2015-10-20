package org.protege.editor.owl.model.repository.extractors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.protege.owlapi.util.IOUtils;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.syntax.RDFParser;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

public class RdfXmlExtractor implements OntologyIdExtractor {

    private final Logger logger = LoggerFactory.getLogger(RdfXmlExtractor.class);

    private URI location;

    public OWLOntologyID getOntologyId() {
        RdfExtractorConsumer consumer = new RdfExtractorConsumer();
        RDFParser parser = new RDFParser();
        InputStream iStream = null;
        try {
        	iStream = IOUtils.getInputStream(location);
            InputSource is = new InputSource(iStream);
            is.setSystemId(location.toURL().toString());
            parser.parse(is, consumer);
        }
        catch (Throwable t) {
            logger.debug("Exception caught trying to extract ontology from rdf file at  " + location, t);
            return null;
        }
        finally {
        	if (iStream != null) {
        		try {
        			iStream.close();
        		}
        		catch (IOException ioe) {
        			logger.warn("Could not close open stream", ioe);
        		}
        	}
        }
        return consumer.getOntologyID();
    }

    public void setPhysicalAddress(URI location) {
        this.location = location;
    }

}
