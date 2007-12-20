package org.protege.editor.owl.model.inference;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntologyManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public class NoOpReasonerFactory implements ProtegeOWLReasonerFactory {

    public void setup(OWLOntologyManager manager, String id, String name) {
    }


    public String getReasonerId() {
        return "NoOpReasoner";
    }


    public String getReasonerName() {
        return "None";
    }


    public OWLReasoner createReasoner(OWLOntologyManager owlOntologyManager) {
        return new NoOpReasoner(owlOntologyManager);
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }


    public boolean requiresExplicitClassification() {
        return false;
    }



}
