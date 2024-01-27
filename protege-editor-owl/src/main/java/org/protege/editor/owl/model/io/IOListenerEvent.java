package org.protege.editor.owl.model.io;

import java.net.URI;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */

import org.semanticweb.owlapi.model.OWLOntologyID;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 16-Sep-2008<br><br>
 */
public class IOListenerEvent {

    private OWLOntologyID ontologyID;

    private URI physicalURI;


    public IOListenerEvent(OWLOntologyID ontologyID, URI physicalURI) {
        this.ontologyID = ontologyID;
        this.physicalURI = physicalURI;
    }


    public OWLOntologyID getOntologyID() {
        return ontologyID;
    }


    public URI getPhysicalURI() {
        return physicalURI;
    }
}
