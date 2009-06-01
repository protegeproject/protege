package org.protege.editor.owl.model.io;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.model.OWLOntologyIRIMapper;
import org.semanticweb.owl.util.SimpleIRIMapper;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
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
 * It looks in auto-mapped libraries.  These are folder libraries
 * that correspond to folders where the "root ontologies" have been
 * loaded from.  If the mapper finds an ontology that has a mapping
 * to one of these auto-mapped files, then the URI of the
 * auto-mapped file is returned.
 */
public class AutoMappedRepositoryIRIMapper implements OWLOntologyIRIMapper {

    private Logger logger = Logger.getLogger(AutoMappedRepositoryIRIMapper.class);

    private OWLModelManager mngr;

    private Set<OntologyLibrary> automappedLibraries = new HashSet<OntologyLibrary>();


    public AutoMappedRepositoryIRIMapper(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public URI getPhysicalURI(IRI logicalURI) {
        URI uri;
        // Search auto mapped libraries
        for (OntologyLibrary lib : automappedLibraries) {
            if (lib.contains(logicalURI)) {
                uri = lib.getPhysicalURI(logicalURI);
                // Map the URI
                mngr.getOWLOntologyManager().addIRIMapper(new SimpleIRIMapper(logicalURI, uri));
                if (logger.isInfoEnabled()) {
                    logger.info("Mapping (from automapping): " + lib.getClassExpression() + "): " + logicalURI + " -> " + uri);
                }
                return uri;
            }
        }
        return null;
    }


    public void addLibrary(OntologyLibrary ontologyLibrary) {
        automappedLibraries.add(ontologyLibrary);
    }
}
