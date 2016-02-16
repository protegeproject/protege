/**
 * 
 */
package org.protege.owlapi.inference.orphan;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import java.util.HashSet;
import java.util.Set;

public class NamedClassExtractor extends OWLClassExpressionVisitorAdapter {

    Set<OWLClass> result = new HashSet<>();


    public void reset() {
        result.clear();
    }


    public Set<OWLClass> getResult() {
        return result;
    }


    public void visit(OWLClass desc) {
        result.add(desc);
    }


    public void visit(OWLObjectIntersectionOf desc) {
        for (OWLClassExpression op : desc.getOperands()) {
            op.accept(this);
        }
    }
}