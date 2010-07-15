package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public class NoOpReasonerFactory implements ProtegeOWLReasonerFactory {
    private String id = NULL_REASONER_ID;
    private String name;
    public static final String NULL_REASONER_ID = "org.protege.editor.owl.NoOpReasoner";

    public void setup(OWLOntologyManager manager, String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getReasonerId() {
        return id;
    }

    public String getReasonerName() {
        return name;
    }


    
    public OWLReasoner createReasoner(OWLOntology ontology, ReasonerProgressMonitor monitor) {
        return new NoOpReasoner(ontology);
    }

    public void initialise() throws Exception {

    }

    public void dispose() throws Exception {

    }

}
