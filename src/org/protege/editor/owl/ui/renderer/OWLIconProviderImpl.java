package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;

import javax.swing.*;
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
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLIconProviderImpl extends OWLObjectVisitorAdapter implements OWLIconProvider {

    private Icon icon;

    private Icon primitiveClassIcon = OWLIcons.getIcon("class.primitive.png");

    private OWLModelManager owlModelManager;

    private int size = 16;


    public OWLIconProviderImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public Icon getIcon() {
        return icon;
    }


    public Icon getIcon(OWLObject owlObject) {
        try {
            icon = null;
            owlObject.accept(this);
            return icon;
        }
        catch (Exception e) {
            return null;
        }
    }


    public void visit(OWLObjectIntersectionOf owlAnd) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDataType owlDataType) {
        icon = OWLIcons.getIcon("datarange.png");
    }


    public void visit(OWLDataOneOf owlDataEnumeration) {
        icon = OWLIcons.getIcon("datarange.png");
    }


    public void visit(OWLDataAllRestriction owlDataAllRestriction) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDataProperty owlDataProperty) {
        icon = OWLIcons.getIcon("property.data.png");
    }


    public void visit(OWLDataSomeRestriction owlDataSomeRestriction) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDataValueRestriction owlDataValueRestriction) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDifferentIndividualsAxiom owlDifferentIndividualsAxiom) {
    }


    public void visit(OWLIndividual owlIndividual) {
        icon = OWLIcons.getIcon("individual.png");
    }


    public void visit(OWLAnonymousIndividual individual) {
        icon = OWLIcons.getIcon("individual.png");
    }


    public void visit(OWLObjectAllRestriction owlObjectAllRestriction) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLObjectMinCardinalityRestriction desc) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLObjectExactCardinalityRestriction desc) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLObjectMaxCardinalityRestriction desc) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLObjectSelfRestriction desc) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDataMinCardinalityRestriction desc) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDataExactCardinalityRestriction desc) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDataMaxCardinalityRestriction desc) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLObjectProperty owlObjectProperty) {
        icon = OWLIcons.getIcon("property.object.png");
    }


    public void visit(OWLObjectSomeRestriction owlObjectSomeRestriction) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLObjectValueRestriction owlObjectValueRestriction) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLObjectComplementOf owlNot) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLOntology owlOntology) {
        icon = OWLIcons.getIcon("ontology.png");
    }


    public void visit(OWLObjectUnionOf owlOr) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLImportsDeclaration axiom) {
        icon = OWLIcons.getIcon("ontology.png");
    }


    public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
        icon = OWLIcons.getIcon("property.object.png");
    }


    public void visit(OWLClass owlClass) {
        for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
            if (owlClass.isDefined(ont)) {
                icon = OWLIcons.getIcon("class.defined.png");
                return;
            }
        }
        icon = primitiveClassIcon;
    }


    public void visit(OWLObjectOneOf owlEnumeration) {
        icon = primitiveClassIcon;
    }
}
