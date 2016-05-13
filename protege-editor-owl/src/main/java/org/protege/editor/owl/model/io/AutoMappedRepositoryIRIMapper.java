package org.protege.editor.owl.model.io;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

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

 * It looks in auto-mapped libraries.  These are folder libraries
 * that correspond to folders where the "root ontologies" have been
 * loaded from.  If the mapper finds an ontology that has a mapping
 * to one of these auto-mapped files, then the URI of the
 * auto-mapped file is returned.
 */
public class AutoMappedRepositoryIRIMapper implements OWLOntologyIRIMapper {

    private final Logger logger = LoggerFactory.getLogger(AutoMappedRepositoryIRIMapper.class);

    private final OntologyCatalogManager ontologyCatalogManager;
    

    public AutoMappedRepositoryIRIMapper(OntologyCatalogManager ontologyCatalogManager) {
        this.ontologyCatalogManager = ontologyCatalogManager;
    }


    public IRI getDocumentIRI(IRI importedIRI) {
    	URI u = ontologyCatalogManager.getRedirect(importedIRI.toURI());
        if (u == null) {
            logger.info("Imported ontology document {} was not resolved to any documents defined in the ontology catalog.", importedIRI);
            return null;
        }
        else {
            logger.info("Imported ontology document {} was resolved to {} by the ontology catalog.", importedIRI, u);
            return IRI.create(u);
        }
    }
}
