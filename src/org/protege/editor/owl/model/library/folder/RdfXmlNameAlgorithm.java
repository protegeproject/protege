package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.rdf.syntax.RDFParser;
import org.xml.sax.InputSource;

public class RdfXmlNameAlgorithm implements Algorithm {
    private static Logger log = Logger.getLogger(RdfXmlNameAlgorithm.class);
    
    private boolean assumeLatest                  = false;
    private Set<String> ontologyProperties        = new HashSet<String>();

    public void setAssumeLatest(boolean assumeLatest) {
        this.assumeLatest = assumeLatest;
    }
    
    public void addOntologyProperty(String property) {
        ontologyProperties.add(property);
    }

    public Set<URI> getSuggestions(File f) {
        RdfExtractorConsumer consumer = new RdfExtractorConsumer();
        for (String property : ontologyProperties) {
            consumer.addOntologyProperty(property);
        }
        RDFParser parser = new RDFParser();
        try {
            InputSource is = new InputSource(new FileInputStream(f));
            is.setSystemId(f.toURI().toString());
            parser.parse(is, consumer);
        }
        catch (SAXParseCompletedException spce) {
        	if (log.isDebugEnabled()) {
        		log.debug("parse completed early", spce);
        	}
        }
        catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Exception caught trying to extract ontology from rdf file at  " + f, t);
            }
            return Collections.emptySet();
        }
        OWLOntologyID id = consumer.getOntologyID();
        Set<URI> results = new HashSet<URI>();
        if (id != null) {
            if (id.getVersionIRI() != null) {
                results.add(id.getVersionIRI().toURI());
                if (assumeLatest) {
                    results.add(id.getOntologyIRI().toURI());
                }
            }
            else {
                results.add(id.getOntologyIRI().toURI());
            }
        }
        return results;
    }

}
