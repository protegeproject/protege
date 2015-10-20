package org.protege.editor.owl.model.io;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import org.slf4j.Logger;
import org.protege.common.CommonProtegeProperties;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;

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
    private OntologyCatalogManager libraryManager;
    

    public AutoMappedRepositoryIRIMapper(OWLModelManager mngr)  {
        libraryManager = mngr.getOntologyCatalogManager();
    }


    public IRI getDocumentIRI(IRI logicalURI) {
    	URI u = libraryManager.getRedirect(logicalURI.toURI());
    	return u != null ? IRI.create(u) : null;
    }
}
