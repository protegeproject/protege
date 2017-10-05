package org.protege.editor.owl.model.io;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 1, 2008<br><br>
 * <p>
 * A custom URIMapper.  This is used by the various parsers to
 * convert ontology URIs into physical URIs that point to concrete
 * representations of ontologies.
 * <p>
 * The mapper uses the following strategy:
 * <p>
 * The system attempts to resolve the imported IRI.  If
 * this succeeds then the imported IRI is returned.
 */
public class WebConnectionIRIMapper implements OWLOntologyIRIMapper {

    private static final int CONNECTION_TIMEOUT = 20000;

    private static final boolean CONNECTION_ACCEPT_HTTP_COMPRESSION = true;

    private static final String HTTP = "http";

    private static final String HTTPS = "https";

    private static final String HEAD = "HEAD";

    private final Logger logger = LoggerFactory.getLogger(WebConnectionIRIMapper.class);

    public IRI getDocumentIRI(@Nonnull IRI ontologyIRI) {
        // We can't find a local version of the ontology. Can we resolve the URI?

        // First check that the URI can be resolved.
        final URI documentURI = ontologyIRI.toURI();
        try {
            URLConnection connection = IOUtils.getUrlConnection(documentURI,
                                                                CONNECTION_ACCEPT_HTTP_COMPRESSION,
                                                                CONNECTION_TIMEOUT);
            // For http(s) schemes see if the requested file is there by requesting the HEAD
            if (HTTP.equals(ontologyIRI.getScheme()) || HTTPS.equals(ontologyIRI.getScheme())) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                httpURLConnection.setRequestMethod(HEAD);
                if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    return ontologyIRI;
                }
            }
            else {
                // For other schemes try to get an inputstream
                InputStream is = connection.getInputStream();
                is.close();
                return ontologyIRI;
            }

        } catch (MalformedURLException e) {
            logger.info("Imported ontology document IRI {} is malformed.", ontologyIRI);
        } catch (FileNotFoundException e) {
            logger.info("Imported ontology document {} does not exist on the Web (File Not Found).", ontologyIRI);
        } catch (UnknownHostException e) {
            String host = e.getMessage();
            logger.info("Imported ontology document {} could not be retrieved. Cannot connect to {} (Unknown Host).",
                        ontologyIRI,
                        host);
        } catch (IOException e) {
            // Can't open the stream - problem resolving the URI
            logger.info("Imported ontology document {} could not be retrieved: {}", ontologyIRI, e.getMessage());
        }
        return null;
    }
}