package org.protege.editor.owl.model.parser;

import org.protege.editor.owl.model.find.OWLEntityFinder;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 8, 2008<br><br>
 */
public class ProtegeOWLEntityChecker implements OWLEntityChecker {

    private OWLEntityFinder finder;


    public ProtegeOWLEntityChecker(OWLEntityFinder finder) {
        this.finder = finder;
    }


    public OWLClass getOWLClass(String rendering) {
        return finder.getOWLClass(rendering);
    }


    public OWLObjectProperty getOWLObjectProperty(String rendering) {
        return finder.getOWLObjectProperty(rendering);
    }


    public OWLDataProperty getOWLDataProperty(String rendering) {
        return finder.getOWLDataProperty(rendering);
    }


    public OWLNamedIndividual getOWLIndividual(String rendering) {
        return finder.getOWLIndividual(rendering);
    }


    public OWLDatatype getOWLDatatype(String rendering) {
        return finder.getOWLDatatype(rendering);
    }


    public OWLAnnotationProperty getOWLAnnotationProperty(String rendering) {
        return finder.getOWLAnnotationProperty(rendering);
    }
}
