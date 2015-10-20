package org.protege.editor.owl.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.slf4j.LoggerFactory;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 1, 2008<br><br>
 *
 * A custom URIMapper.  This is used by the various parsers to
 * convert ontology URIs into physical URIs that point to concrete
 * representations of ontologies.
 * <p/>
 * The mapper uses the following strategy:
 * <p/>
 * The system attemps to resolve the logical URI.  If
 * this succeeds then the logical URI is returned.

 */
public class WebConnectionIRIMapper implements OWLOntologyIRIMapper {

    private final Logger logger = LoggerFactory.getLogger(WebConnectionIRIMapper.class);


    public IRI getDocumentIRI(IRI ontologyIRI) {
        // We can't find a local version of the ontology. Can we resolve the URI?
        try {
            // First check that the URI can be resolved.
            final URI potentialPhysicalURI = ontologyIRI.toURI();
            URLConnection conn = potentialPhysicalURI.toURL().openConnection();
            InputStream is = conn.getInputStream();
            is.close();
            return IRI.create(potentialPhysicalURI);
        }
        catch (IOException e) {
            // Can't open the stream - problem resolving the URI
            logger.error("Cannot open a stream to {}.", ontologyIRI, e);
            // Delegate to the missing imports handler
        }
        return null;
    }
}