package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public abstract class ProtegeOWLReasonerFactoryAdapter implements ProtegeOWLReasonerFactory {

    private String id;

    private String name;

    private OWLOntologyManager owlOntologyManager;


    public void setup(OWLOntologyManager manager, String id, String name) {
        this.id = id;
        this.name = name;
        this.owlOntologyManager = manager;
    }


    public OWLOntologyManager getOWLOntologyManager() {
        return owlOntologyManager;
    }


    public String getReasonerId() {
        return id;
    }


    public String getReasonerName() {
        return name;
    }
}
