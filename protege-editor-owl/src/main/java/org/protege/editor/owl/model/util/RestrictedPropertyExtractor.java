package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
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
public class RestrictedPropertyExtractor implements OWLClassExpressionVisitor {

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


    @Override
    public void visit(@Nonnull OWLObjectIntersectionOf node) {
        for (OWLClassExpression desc : node.getOperands()) {
            desc.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLObjectComplementOf node) {
        node.getOperand().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLObjectUnionOf node) {
        for (OWLClassExpression desc : node.getOperands()) {
            desc.accept(this);
        }
    }

    @Override
    public void visit(@Nonnull OWLDataAllValuesFrom node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLDataSomeValuesFrom node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLDataHasValue node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLObjectAllValuesFrom node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLObjectSomeValuesFrom node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLObjectHasValue node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLObjectMinCardinality node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLObjectExactCardinality node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLObjectMaxCardinality node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLObjectHasSelf node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLDataMinCardinality node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLDataExactCardinality node) {
        properties.add(node.getProperty());
    }

    @Override
    public void visit(@Nonnull OWLDataMaxCardinality node) {
        properties.add(node.getProperty());
    }
}
