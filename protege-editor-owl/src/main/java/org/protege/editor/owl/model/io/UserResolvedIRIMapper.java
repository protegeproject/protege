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
*
*/

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

 * The system turns to the "Missing Import Handler", which may
 * try to obtain the physical URI (usually by adding a library or
 * by specifying a file etc.)
 */
public class UserResolvedIRIMapper implements OWLOntologyIRIMapper {

    private Map<IRI, URI> resolvedMissingImports = new HashMap<>();

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
            return resolvedURI != null ? IRI.create(resolvedURI) : null;
        }
    }

    private URI resolveMissingImport(IRI ontologyIRI) {
        IRI iri = missingImportHandler.getDocumentIRI(ontologyIRI);
        return iri != null ? iri.toURI() : null;
    }


    public void setMissingImportHandler(MissingImportHandler handler) {
        missingImportHandler = handler;
    }
}
