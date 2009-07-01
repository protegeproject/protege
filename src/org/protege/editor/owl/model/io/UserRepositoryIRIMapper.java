package org.protege.editor.owl.model.io;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.protege.editor.owl.model.library.OntologyLibraryManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

import java.net.URI;
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
 * It looks in the ontology libraries.  If an ontology library
 * contains an ontology that has the logical URI then the library
 * is asked for the physical URI and this URI is returned.
 */
public class UserRepositoryIRIMapper implements OWLOntologyIRIMapper {

    private Logger logger = Logger.getLogger(UserRepositoryIRIMapper.class);

    private OWLModelManager mngr;


    public UserRepositoryIRIMapper(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public URI getPhysicalURI(IRI ontologyIRI) {
        URI uri;
        // Search user defined libraries
        OntologyLibraryManager manager = mngr.getOntologyLibraryManager();
        OntologyLibrary lib = manager.getLibrary(ontologyIRI);
        if (lib != null) {
            uri = lib.getPhysicalURI(ontologyIRI);
            if (logger.isInfoEnabled()) {
                logger.info("Mapping (from library: " + lib.getClassExpression() + "): " + ontologyIRI + " -> " + uri);
            }
            return lib.getPhysicalURI(ontologyIRI);
        }
        if (logger.isInfoEnabled()) {
            logger.info("No mapping for " + ontologyIRI + " found.");
        }

        return null;
    }
}