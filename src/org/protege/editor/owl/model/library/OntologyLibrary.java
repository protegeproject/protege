package org.protege.editor.owl.model.library;

import java.net.URI;
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
    public String getDescription();


    /**
     * Gets the ontologies that this library contains.
     * @return A <code>Set</code> of ontology URIs that
     *         identify the ontologies that this library contains.
     */
    public Set<URI> getOntologyURIs();


    /**
     * Determines whether or not the library contains an
     * ontology.
     * @param ontologyURI The ontology URI (logical URI) that
     *                    identifies the ontology.
     * @return <code>true</code> if the library contains the ontology,
     *         <code>false</code> if the library doesn't contain the ontology.
     */
    public boolean contains(URI ontologyURI);


    /**
     * Gets the physical URI which can be used to obtain a
     * stream in order to load the ontology.
     * @param ontologyURI The ontology URI (logical URI) that
     *                    identifies the ontology whose physical URI is to be obtained.
     */
    public URI getPhysicalURI(URI ontologyURI);


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
