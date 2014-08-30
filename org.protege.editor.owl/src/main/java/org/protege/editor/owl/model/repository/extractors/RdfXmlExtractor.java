package org.protege.editor.owl.model.repository.extractors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.log4j.Logger;
import org.protege.owlapi.util.IOUtils;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFParser;
import org.xml.sax.InputSource;

public class RdfXmlExtractor implements OntologyIdExtractor {
    private Logger log = Logger.getLogger(RdfXmlExtractor.class);
    private URI location;

    @Override
    public OWLOntologyID getOntologyId() {
        RdfExtractorConsumer consumer = new RdfExtractorConsumer();
        RDFParser parser = new RDFParser();
        InputStream iStream = null;
        try {
            OWLOntologyLoaderConfiguration owlOntologyLoaderConfiguration = new OWLOntologyLoaderConfiguration();
            iStream = IOUtils.getInputStream(location,
 owlOntologyLoaderConfiguration
                            .isAcceptingHTTPCompression(),
                            owlOntologyLoaderConfiguration
                                    .getConnectionTimeout());
            InputSource is = new InputSource(iStream);
            is.setSystemId(location.toURL().toString());
            parser.parse(is, consumer);
        }
        catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Exception caught trying to extract ontology from rdf file at  " + location, t);
            }
            return null;
        }
        finally {
        	if (iStream != null) {
        		try {
        			iStream.close();
        		}
        		catch (IOException ioe) {
        			log.warn("Could not close open stream", ioe);
        		}
        	}
        }
        return consumer.getOntologyID();
    }

    @Override
    public void setPhysicalAddress(URI location) {
        this.location = location;
    }

}
