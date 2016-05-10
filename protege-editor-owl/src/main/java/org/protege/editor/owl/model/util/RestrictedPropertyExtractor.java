package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A visitor that collects the properties that are
 * restricted in <code>OWLRestriction</code>s.  This visitor
 * visits <code>OWLClassExpression</code>s.  As it goes, it accumulates
 * all of the properties that are used in any restrictions that it
 * visits.  Note that <code>OWLAnd</code>, <code>OWLOr</code> and <code>OWLNot</code> descriptions are "flattened"
 * out, so any restrictions that are contained as one of their operands
 * are also visited.
 */
public class RestrictedPropertyExtractor extends OWLClassExpressionVisitorAdapter {

    private Set<OWLPropertyExpression> properties;


    public RestrictedPropertyExtractor() {
        properties = new HashSet<>();
    }


    public Set<OWLPropertyExpression> getRestrictedProperties() {
        return new HashSet<>(properties);
    }


    public void reset() {
        properties.clear();
    }


    public void visit(OWLObjectIntersectionOf node) {
        for (OWLClassExpression desc : node.getOperands()) {
            desc.accept(this);
        }
    }


    public void visit(OWLObjectComplementOf node) {
        node.getOperand().accept(this);
    }


    public void visit(OWLObjectUnionOf node) {
        for (OWLClassExpression desc : node.getOperands()) {
            desc.accept(this);
        }
    }


    public void visit(OWLDataAllValuesFrom node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataSomeValuesFrom node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataHasValue node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectAllValuesFrom node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectSomeValuesFrom node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectHasValue node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectMinCardinality node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectExactCardinality node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectMaxCardinality node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectHasSelf node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataMinCardinality node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataExactCardinality node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataMaxCardinality node) {
        properties.add(node.getProperty());
    }
}
