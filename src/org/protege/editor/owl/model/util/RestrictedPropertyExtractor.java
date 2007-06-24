package org.protege.editor.owl.model.util;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLPropertyExpression;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A visitor that collects the properties that are
 * restricted in <code>OWLRestriction</code>s.  This visitor
 * visits <code>OWLDescription</code>s.  As it goes, it accumulates
 * all of the properties that are used in any restrictions that it
 * visits.  Note that <code>OWLAnd</code>, <code>OWLOr</code> and <code>OWLNot</code> descriptions are "flattened"
 * out, so any restrictions that are contained as one of their operands
 * are also visited.
 */
public class RestrictedPropertyExtractor extends OWLDescriptionVisitorAdapter {

    private Set<OWLPropertyExpression> properties;


    public RestrictedPropertyExtractor() {
        properties = new HashSet<OWLPropertyExpression>();
    }


    public Set<OWLPropertyExpression> getRestrictedProperties() {
        return new HashSet<OWLPropertyExpression>(properties);
    }


    public void reset() {
        properties.clear();
    }


    public void visit(OWLObjectIntersectionOf node) {
        for (OWLDescription desc : node.getOperands()) {
            desc.accept(this);
        }
    }


    public void visit(OWLObjectComplementOf node) {
        node.getOperand().accept(this);
    }


    public void visit(OWLObjectUnionOf node) {
        for (OWLDescription desc : node.getOperands()) {
            desc.accept(this);
        }
    }


    public void visit(OWLDataAllRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataSomeRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataValueRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectAllRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectSomeRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectValueRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectMinCardinalityRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectExactCardinalityRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectMaxCardinalityRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLObjectSelfRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataMinCardinalityRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataExactCardinalityRestriction node) {
        properties.add(node.getProperty());
    }


    public void visit(OWLDataMaxCardinalityRestriction node) {
        properties.add(node.getProperty());
    }
}
