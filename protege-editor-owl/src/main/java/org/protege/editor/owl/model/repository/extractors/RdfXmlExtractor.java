package org.protege.editor.owl.model.repository.extractors;

import com.google.common.base.Optional;
import org.protege.editor.owl.model.io.IOUtils;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URI;

public class RdfXmlExtractor implements OntologyIdExtractor {

    private final Logger logger = LoggerFactory.getLogger(RdfXmlExtractor.class);

    public Optional<OWLOntologyID> getOntologyId(URI location) {
        RdfExtractorConsumer consumer = new RdfExtractorConsumer();
        RDFParser parser = new RDFParser();
        try (InputStream iStream = IOUtils.getInputStream(location, true, 30000)) {
            InputSource is = new InputSource(iStream);
            is.setSystemId(location.toURL().toString());
            parser.parse(is, consumer);
            return consumer.getOntologyID();
        } catch (Throwable t) {
            logger.debug("Exception caught trying to extract ontology from rdf file at  " + location, t);
            return Optional.absent();
        }
    }
}
