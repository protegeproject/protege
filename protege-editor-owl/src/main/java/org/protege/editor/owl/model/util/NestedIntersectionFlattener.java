package org.protege.editor.owl.model.util;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;


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
