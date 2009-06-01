package org.protege.editor.owl.model.io;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.repository.OntologyIRIExtractor;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.model.OWLOntologyIRIMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

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

    private Logger logger = Logger.getLogger(WebConnectionIRIMapper.class);


    public URI getPhysicalURI(IRI ontologyIRI) {
        // We can't find a local version of the ontology. Can we resolve the URI?
        try {
            // First check that the URI can be resolved.
            final URI potentialPhysicalURI = ontologyIRI.toURI();
            URLConnection conn = potentialPhysicalURI.toURL().openConnection();
            conn.setReadTimeout(5000);
            InputStream is = conn.getInputStream();
            is.close();
            // Opened a stream.  Is it an ontology at the URI?
            OntologyIRIExtractor ext = new OntologyIRIExtractor(potentialPhysicalURI);
            ext.getOntologyIRI();
            if (ext.isStartElementPresent()) {
                // There is an ontology at the URI!
                return potentialPhysicalURI;
            }
        }
        catch (IOException e) {
            // Can't open the stream - problem resolving the URI
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            // Delegate to the missing imports handler
        }
        return null;
    }
}