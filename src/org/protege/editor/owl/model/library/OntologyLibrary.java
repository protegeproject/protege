package org.protege.editor.owl.model.library;

import org.semanticweb.owlapi.model.IRI;

import java.net.URI;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A library contains a set of ontologies.  Each ontology
 * is intuitvely identified by a logical URI - the ontology URI.
 * If a library contains an ontology then the library can provide
 * a physical URI for that ontology.
 */
public interface OntologyLibrary {

    /**
     * Gets a description of the library, which can be
     * used in user interfaces.
     */
    public String getClassExpression();


    /**
     * Gets the ontologies that this library contains.
     * @return A <code>Set</code> of ontology IRIs that
     *         identify the ontologies that this library contains.
     */
    public Set<IRI> getOntologyIRIs();


    /**
     * Determines whether or not the library contains an
     * ontology.
     * @param ontologyIRI The ontology IRI (not physical URI) that
     *                    identifies the ontology.
     * @return <code>true</code> if the library contains the ontology,
     *         <code>false</code> if the library doesn't contain the ontology.
     */
    public boolean contains(IRI ontologyIRI);


    /**
     * Gets the physical IRI which can be used to obtain a
     * stream in order to load the ontology.
     * @param ontologyIRI The ontology IRI (not physical URI) that
     *                    identifies the ontology whose physical URI is to be obtained.
     */
    public URI getPhysicalURI(IRI ontologyIRI);


    /**
     * If the library caches information, then this method rebuilds
     * the cache and refreshes the library.
     */
    public void rebuild();


    /**
     * Gets a memento that can be used to persist the library.
     */
    public OntologyLibraryMemento getMemento();
}
