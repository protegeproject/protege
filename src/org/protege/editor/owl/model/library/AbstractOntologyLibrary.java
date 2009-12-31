package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.protege.xmlcatalog.CatalogUtilities;
import org.semanticweb.owlapi.model.IRI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractOntologyLibrary implements OntologyLibrary {
	
    public boolean contains(IRI ontologyIRI) {
    	return getPhysicalURI(ontologyIRI) != null;
    }
    
    public URI getPhysicalURI(IRI ontologyIRI) {
        return CatalogUtilities.getRedirect(ontologyIRI.toURI(), getXmlCatalog());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClassExpression());
        return builder.toString();
    }
    
    public void save() throws IOException {
        CatalogUtilities.save(getXmlCatalog(), new File(getXmlCatalogName()));
    }
}
