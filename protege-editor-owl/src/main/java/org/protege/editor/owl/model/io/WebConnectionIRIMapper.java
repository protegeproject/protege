package org.protege.editor.owl.model.io;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 1, 2008<br><br>
 *
 * A custom URIMapper.  This is used by the various parsers to
 * convert ontology URIs into physical URIs that point to concrete
 * representations of ontologies.

 * The mapper uses the following strategy:

 * The system attempts to resolve the imported IRI.  If
 * this succeeds then the imported IRI is returned.

 */
public class WebConnectionIRIMapper implements OWLOntologyIRIMapper {

    private final Logger logger = LoggerFactory.getLogger(WebConnectionIRIMapper.class);

    public IRI getDocumentIRI(IRI ontologyIRI) {
        // We can't find a local version of the ontology. Can we resolve the URI?

        // First check that the URI can be resolved.
        final URI documentURI = ontologyIRI.toURI();
        try {
            final URL documentURL = documentURI.toURL();
            URLConnection conn = documentURL.openConnection();
            InputStream is = conn.getInputStream();
            is.close();
            return ontologyIRI;
        }
        catch (MalformedURLException e) {
            logger.info("Unresolvable owl:imports: {}.  The imported ontology document IRI is malformed.");
        }
        catch (FileNotFoundException e) {
            logger.info("Unresolvable owl:imports: {}. Checked to see if the imported ontology document exists on the Web, but it does not. (File Not Found)", ontologyIRI);
        }
        catch (UnknownHostException e) {
            String host = e.getMessage();
            logger.info("Unresolvable owl:imports: {}. Cannot connect to {} (Unknown Host).", ontologyIRI, host);
        }
        catch (IOException e) {
            // Can't open the stream - problem resolving the URI
            logger.info("Tried, but could not open a stream to imported ontology IRI {}.", ontologyIRI, e);
        }
        return null;
    }
}