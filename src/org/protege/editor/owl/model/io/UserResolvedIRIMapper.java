package org.protege.editor.owl.model.io;

import org.protege.editor.owl.model.MissingImportHandler;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
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
 * The system turns to the "Missing Import Handler", which may
 * try to obtain the physical URI (usually by adding a library or
 * by specifying a file etc.)
 */
public class UserResolvedIRIMapper implements OWLOntologyIRIMapper {

    private Map<IRI, URI> resolvedMissingImports = new HashMap<IRI, URI>();

    private MissingImportHandler missingImportHandler;


    public UserResolvedIRIMapper(MissingImportHandler missingImportHandler) {
        this.missingImportHandler = missingImportHandler;
    }


    public IRI getDocumentIRI(IRI ontologyIRI) {
        if (resolvedMissingImports.containsKey(ontologyIRI)) {
            // Already resolved the missing import - don't ask again
            return IRI.create(resolvedMissingImports.get(ontologyIRI));
        }
        else {

            URI resolvedURI = resolveMissingImport(ontologyIRI);
            if (resolvedURI != null) {
                resolvedMissingImports.put(ontologyIRI, resolvedURI);
            }
            return IRI.create(resolvedURI);
        }
    }

    private URI resolveMissingImport(IRI ontologyIRI) {
        return missingImportHandler.getDocumentIRI(ontologyIRI).toURI();
    }


    public void setMissingImportHandler(MissingImportHandler handler) {
        missingImportHandler = handler;
    }
}