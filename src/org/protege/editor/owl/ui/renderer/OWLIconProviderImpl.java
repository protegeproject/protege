package org.protege.editor.owl.ui.renderer;

import javax.swing.Icon;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLObjectVisitorAdapter;


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

    private Icon individualIcon = OWLIcons.getIcon("individual.png");;


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
        icon = individualIcon;
    }


    public void visit(OWLAnonymousIndividual individual) {
        icon = individualIcon;
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
        if (owlModelManager.getOWLOntologyManager().contains(axiom.getImportedOntologyURI())) {
            icon = OWLIcons.getIcon("ontology.png");
        }
        else {
            icon = OWLIcons.getIcon("ontology.missing.png");
        }
    }


    public void visit(OWLSubClassAxiom owlSubClassAxiom) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDisjointClassesAxiom owlDisjointClassesAxiom) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLDisjointUnionAxiom owlDisjointUnionAxiom) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLEquivalentClassesAxiom owlEquivalentClassesAxiom) {
        icon = primitiveClassIcon;
    }


    public void visit(OWLSameIndividualsAxiom owlSameIndividualsAxiom) {
        icon = individualIcon;
    }

    
    public void visit(OWLClassAssertionAxiom owlClassAssertionAxiom) {
        icon = individualIcon;
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
