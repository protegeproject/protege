/**
 * 
 */
package org.protege.owlapi.inference.orphan;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class NamedClassExtractor implements OWLClassExpressionVisitor {

    Set<OWLClass> result = new HashSet<>();


    public void reset() {
        result.clear();
    }


    public Set<OWLClass> getResult() {
        return result;
    }

    @Override
    public void visit(@Nonnull OWLClass desc) {
        result.add(desc);
    }

    @Override
    public void visit(@Nonnull OWLObjectIntersectionOf desc) {
        for (OWLClassExpression op : desc.getOperands()) {
            op.accept(this);
        }
    }
}