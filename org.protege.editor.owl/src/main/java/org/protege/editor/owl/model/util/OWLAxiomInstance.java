package org.protege.editor.owl.model.util;

import org.protege.editor.owl.model.AnnotationContainer;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
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
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 8, 2009<br><br>
 * 
 * This class is a pair that describes an instance of an axiom with resppect to
 * its containing ontology
 */
public class OWLAxiomInstance implements AnnotationContainer {

    private OWLAxiom ax;

    private OWLOntology ont;


    public OWLAxiomInstance(OWLAxiom ax, OWLOntology ont) {
        this.ax = ax;
        this.ont = ont;
    }


    public OWLAxiom getAxiom() {
        return ax;
    }


    public OWLOntology getOntology() {
        return ont;
    }


    public Set<OWLAnnotation> getAnnotations() {
        return ax.getAnnotations();
    }
}
