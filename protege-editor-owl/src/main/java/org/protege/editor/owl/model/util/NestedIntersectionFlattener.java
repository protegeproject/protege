package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.*;

import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A visitor that may be used to "flatten" <code>OWLClassExpression</code>s.
 * The visitor collects <code>OWLClassExpression</code>s and operands of
 * <code>OWLAnd</code> classes.  For example the description:
 * <code>
 * A and (B and C) and (D or E) and F
 * </code>
 * would be flattened to the set <code>{A, B, C, (D or E), F}</code>.

 * The general pattern of usage is to visit several descriptions and
 * which accumulates the set of flattened descriptions.  These can
 * be obtained with the <code>getClassExpressions</code> method.
 */
public class NestedIntersectionFlattener implements OWLClassExpressionVisitor {

    private Set<OWLClassExpression> descriptions;


    public NestedIntersectionFlattener() {
        descriptions = new HashSet<>();
    }


    public void reset() {
        descriptions.clear();
    }


    public Set<OWLClassExpression> getClassExpressions() {
        return descriptions;
    }


    public void visit(OWLObjectIntersectionOf node) {
        for (OWLClassExpression desc : node.getOperands()) {
            desc.accept(this);
        }
    }


    public void visit(OWLDataAllValuesFrom node) {
        descriptions.add(node);
    }


    public void visit(OWLDataSomeValuesFrom node) {
        descriptions.add(node);
    }


    public void visit(OWLDataHasValue node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectAllValuesFrom node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectSomeValuesFrom node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectHasValue node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectComplementOf node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectUnionOf node) {
        descriptions.add(node);
    }


    public void visit(OWLClass node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectOneOf node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectMinCardinality desc) {
        descriptions.add(desc);
    }


    public void visit(OWLObjectExactCardinality desc) {
        descriptions.add(desc);
    }


    public void visit(OWLObjectMaxCardinality desc) {
        descriptions.add(desc);
    }


    public void visit(OWLObjectHasSelf desc) {
        descriptions.add(desc);
    }


    public void visit(OWLDataMinCardinality desc) {
        descriptions.add(desc);
    }


    public void visit(OWLDataExactCardinality desc) {
        descriptions.add(desc);
    }


    public void visit(OWLDataMaxCardinality desc) {
        descriptions.add(desc);
    }
}
