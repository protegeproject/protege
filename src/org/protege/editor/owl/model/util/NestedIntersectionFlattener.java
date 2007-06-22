package org.protege.editor.owl.model.util;

import org.semanticweb.owl.model.*;

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
