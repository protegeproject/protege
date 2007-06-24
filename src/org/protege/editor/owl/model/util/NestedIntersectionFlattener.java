package org.protege.editor.owl.model.util;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDescriptionVisitor;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectComplementOf;
import org.semanticweb.owl.model.OWLObjectExactCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLObjectMaxCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectMinCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectOneOf;
import org.semanticweb.owl.model.OWLObjectSelfRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectUnionOf;
import org.semanticweb.owl.model.OWLObjectValueRestriction;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A visitor that may be used to "flatten" <code>OWLDescription</code>s.
 * The visitor collects <code>OWLDescription</code>s and operands of
 * <code>OWLAnd</code> classes.  For example the description:
 * <code>
 * A and (B and C) and (D or E) and F
 * </code>
 * would be flattened to the set <code>{A, B, C, (D or E), F}</code>.
 * <p/>
 * The general pattern of usage is to visit several descriptions and
 * which accumulates the set of flattened descriptions.  These can
 * be obtained with the <code>getDescriptions</code> method.
 */
public class NestedIntersectionFlattener implements OWLDescriptionVisitor {

    private Set<OWLDescription> descriptions;


    public NestedIntersectionFlattener() {
        descriptions = new HashSet<OWLDescription>();
    }


    public void reset() {
        descriptions.clear();
    }


    public Set<OWLDescription> getDescriptions() {
        return descriptions;
    }


    public void visit(OWLObjectIntersectionOf node) {
        for (OWLDescription desc : node.getOperands()) {
            desc.accept(this);
        }
    }


    public void visit(OWLDataAllRestriction node) {
        descriptions.add(node);
    }


    public void visit(OWLDataSomeRestriction node) {
        descriptions.add(node);
    }


    public void visit(OWLDataValueRestriction node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectAllRestriction node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectSomeRestriction node) {
        descriptions.add(node);
    }


    public void visit(OWLObjectValueRestriction node) {
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


    public void visit(OWLObjectMinCardinalityRestriction desc) {
        descriptions.add(desc);
    }


    public void visit(OWLObjectExactCardinalityRestriction desc) {
        descriptions.add(desc);
    }


    public void visit(OWLObjectMaxCardinalityRestriction desc) {
        descriptions.add(desc);
    }


    public void visit(OWLObjectSelfRestriction desc) {
        descriptions.add(desc);
    }


    public void visit(OWLDataMinCardinalityRestriction desc) {
        descriptions.add(desc);
    }


    public void visit(OWLDataExactCardinalityRestriction desc) {
        descriptions.add(desc);
    }


    public void visit(OWLDataMaxCardinalityRestriction desc) {
        descriptions.add(desc);
    }
}
