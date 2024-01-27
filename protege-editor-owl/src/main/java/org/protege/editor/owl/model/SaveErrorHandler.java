package org.protege.editor.owl.model;

import java.net.URI;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 14, 2008<br><br>
 */
public interface SaveErrorHandler {

    /**
     *
     * @param ont
     * @param physicalURIForOntology
     * @param e
     * @throws Exception
     */
    void handleErrorSavingOntology(OWLOntology ont, URI physicalURIForOntology, OWLOntologyStorageException e) throws Exception;
}
