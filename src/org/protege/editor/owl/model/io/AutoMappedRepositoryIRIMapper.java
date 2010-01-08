package org.protege.editor.owl.model.io;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

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


    public IRI getDocumentIRI(IRI logicalURI) {
    	List<OntologyLibrary> toRemove = new ArrayList<OntologyLibrary>();
    	URI uri;
    	// Search auto mapped libraries
    	for (OntologyLibrary lib : automappedLibraries) {
    	    if (lib.contains(logicalURI)) {
    	        uri = lib.getPhysicalURI(logicalURI);
    	        // Map the URI
    	        mngr.getOWLOntologyManager().addIRIMapper(new SimpleIRIMapper(logicalURI, IRI.create(uri)));
    	        if (logger.isInfoEnabled()) {
    	            logger.info("Mapping (from automapping): " + lib.getClassExpression() + "): " + logicalURI + " -> " + uri);
    	        }
    	        return IRI.create(uri);
    	    }
    	}
    	automappedLibraries.removeAll(toRemove);
        return null;
    }


    public boolean addLibrary(OntologyLibrary ontologyLibrary) {
        return automappedLibraries.add(ontologyLibrary);
    }
    
    public boolean removeLibrary(OntologyLibrary ontologyLibrary) {
        return automappedLibraries.remove(ontologyLibrary);
    }
}
