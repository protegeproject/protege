package org.protege.owlapi.inference.cls;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

    /**
     * Checks whether a class description contains a specified named conjunct.
     */
public class NamedConjunctChecker extends OWLClassExpressionVisitorAdapter {

    private boolean found;

    private OWLClass searchClass;


    public boolean containsConjunct(OWLClass conjunct, OWLClassExpression description) {
        found = false;
        searchClass = conjunct;
        description.accept(this);
        return found;
    }

    //////////////////////////////////////////////////////////////////////////////////////////


    public void visit(OWLClass desc) {
        if (desc.equals(searchClass)) {
            found = true;
        }
    }


    public void visit(OWLObjectIntersectionOf desc) {
        for (OWLClassExpression op : desc.getOperands()) {
            op.accept(this);
            if (found) {
                break;
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
}