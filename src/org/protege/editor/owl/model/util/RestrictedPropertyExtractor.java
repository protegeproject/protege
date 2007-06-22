package org.protege.editor.owl.model.util;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;

import java.util.HashSet;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
