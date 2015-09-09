package org.protege.editor.owl.ui.error;

import org.semanticweb.owlapi.model.OWLOntologyID;

import java.net.URI;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 28, 2008<br><br>
 *
 * A hook to allow for feedback when an error occurs during loading
 */
public interface OntologyLoadErrorHandler {

    /**
     *
     * @param ontologyID the ID of the ontology that failed to load
     * @param loc the location the ontology failed to load from
     * @param e the exception thrown by the OWL API
     * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException rethrow if the error needs to be reported further
     */
    <T extends Throwable> void handleErrorLoadingOntology(OWLOntologyID ontologyID, URI loc, T e) throws Throwable;
}
