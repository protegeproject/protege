package org.protege.editor.owl.model;

import org.semanticweb.owlapi.model.OWLOntologyID;

import java.net.URI;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 17 May 16
 */
public interface IOListenerManager {

    void fireBeforeLoadEvent(OWLOntologyID ontologyID, URI documentURI);

    void fireAfterLoadEvent(OWLOntologyID ontologyID, URI documentURI);

    void fireBeforeSaveEvent(OWLOntologyID ontologyID, URI documentURI);

    void fireAfterSaveEvent(OWLOntologyID ontologyID, URI documentURI);
}
