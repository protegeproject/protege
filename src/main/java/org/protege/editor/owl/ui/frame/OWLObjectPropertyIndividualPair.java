package org.protege.editor.owl.ui.frame;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 * <p/>
 * A utility class which binds an object property expression and individual together.
 */
public class OWLObjectPropertyIndividualPair {

    private OWLObjectPropertyExpression property;

    private OWLIndividual individual;


    public OWLObjectPropertyIndividualPair(OWLObjectPropertyExpression property, OWLIndividual individual) {
        this.property = property;
        this.individual = individual;
    }


    public OWLObjectPropertyExpression getProperty() {
        return property;
    }


    public OWLIndividual getIndividual() {
        return individual;
    }
}
