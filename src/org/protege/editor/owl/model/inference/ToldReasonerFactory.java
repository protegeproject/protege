package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.inference.OWLReasoner;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Apr-2007<br><br>
 */
public class ToldReasonerFactory extends ProtegeOWLReasonerFactoryAdapter {

    public OWLReasoner createReasoner(OWLOntologyManager owlOntologyManager, Set<OWLOntology> owlOntologies) {
        return null;
    }
    

    public OWLReasoner createReasoner(OWLOntologyManager owlOntologyManager) {
        return null;
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }


    public boolean requiresExplicitClassification() {
        return false;
    }
}
