package org.protege.editor.owl.model;

import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 8, 2009<br><br>
 */
public class OntologyAnnotationContainer implements AnnotationContainer {

    private OWLOntology ont;


    public OntologyAnnotationContainer(OWLOntology ont) {
        this.ont = ont;
    }

    public OWLOntology getOntology(){
        return ont;
    }

    public Set<OWLAnnotation> getAnnotations() {
        return ont.getAnnotations();
    }
}
