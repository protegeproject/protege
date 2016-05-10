package org.protege.editor.owl.model.io;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 23, 2008<br><br>
 */
public interface OntologySourcesListener {

    void ontologySourcesChanged(OntologySourcesChangeEvent event);


    public class OntologySourcesChangeEvent{

        private Set<OWLOntology> ontologies;


        public OntologySourcesChangeEvent(Set<OWLOntology> ontologies) {
            this.ontologies = ontologies;
        }


        public Set<OWLOntology> getOntologies(){
            return ontologies;
        }
    }
}
