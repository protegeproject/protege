/**
 * 
 */
package org.protege.owlapi.inference.orphan;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

public class NamedClassExtractor extends OWLClassExpressionVisitorAdapter {

    Set<OWLClass> result = new HashSet<OWLClass>();


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