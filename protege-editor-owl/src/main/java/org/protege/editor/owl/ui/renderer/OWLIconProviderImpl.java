package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.protege.editor.owl.ui.renderer.context.DefinedClassChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLIconProviderImpl implements OWLObjectVisitor, OWLIconProvider {

    private Icon icon;

    private final Icon primitiveClassIcon = new OWLClassIcon(OWLClassIcon.Type.PRIMITIVE);//OWLIcons.getIcon("class.primitive.png");

    private final Icon definedClassIcon = new OWLClassIcon(OWLClassIcon.Type.DEFINED);

    private final Icon objectPropertyIcon = new OWLObjectPropertyIcon();

    private final Icon dataPropertyIcon = new OWLDataPropertyIcon();

    private final Icon annotationPropertyIcon = new OWLAnnotationPropertyIcon();

    private final Icon individualIcon = new OWLIndividualIcon(OWLEntityIcon.FillType.FILLED);

    private final Icon dataTypeIcon = new OWLDatatypeIcon();

    private final Icon ontologyIcon = OWLIcons.getIcon("ontology.png");

    private final Icon ontologyMissing = OWLIcons.getIcon("ontology.missing.png");


    private final DefinedClassChecker definedClassChecker;


    @Deprecated
    public OWLIconProviderImpl(@Nonnull final OWLModelManager owlModelManager) {
        definedClassChecker = cls -> {
            for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
                if (isDefined(cls, ont)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Creates an icon provider implementation.
     * @param definedClassChecker A checker that can be used to determine whether or not a class is a defined class.
     */
    public OWLIconProviderImpl(@Nonnull DefinedClassChecker definedClassChecker) {
        this.definedClassChecker = checkNotNull(definedClassChecker);
    }

    private static boolean isDefined(OWLClass owlClass, OWLOntology ontology) {
        if (EntitySearcher.isDefined(owlClass, ontology)) {
            return true;
        }
        Set<OWLDisjointUnionAxiom> axioms = ontology.getDisjointUnionAxioms(owlClass);
        return !axioms.isEmpty();
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

    @Override
    public void visit(@Nonnull OWLObjectIntersectionOf owlAnd) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDatatype owlDatatype) {
        icon = dataTypeIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataOneOf owlDataEnumeration) {
        icon = dataTypeIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataAllValuesFrom owlDataAllRestriction) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataProperty owlDataProperty) {
        icon = dataPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataHasValue owlDataValueRestriction) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDifferentIndividualsAxiom owlDifferentIndividualsAxiom) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLDisjointDataPropertiesAxiom owlDisjointDataPropertiesAxiom) {
        icon = dataPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLHasKeyAxiom owlHasKeyAxiom) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDatatypeDefinitionAxiom owlDatatypeDefinitionAxiom) {
        icon = dataTypeIcon;
    }

    @Override
    public void visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLEquivalentObjectPropertiesAxiom owlEquivalentObjectPropertiesAxiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom owlNegativeDataPropertyAssertionAxiom) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectPropertyAssertionAxiom owlObjectPropertyAssertionAxiom) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLNamedIndividual owlIndividual) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLAnonymousIndividual individual) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectAllValuesFrom owlObjectAllRestriction) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectMinCardinality desc) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectExactCardinality desc) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectMaxCardinality desc) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectHasSelf desc) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataMinCardinality desc) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataExactCardinality desc) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataMaxCardinality desc) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectProperty owlObjectProperty) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectHasValue owlObjectValueRestriction) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectComplementOf owlNot) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLOntology owlOntology) {
        icon = ontologyIcon;
    }

    @Override
    public void visit(@Nonnull OWLObjectUnionOf owlOr) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDeclarationAxiom owlDeclarationAxiom) {
        owlDeclarationAxiom.getEntity().accept(this);
    }

    @Override
    public void visit(@Nonnull OWLSubClassOfAxiom owlSubClassAxiom) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom owlNegativeObjectPropertyAssertionAxiom) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLAsymmetricObjectPropertyAxiom owlAntiSymmetricObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLReflexiveObjectPropertyAxiom owlReflexiveObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLDisjointClassesAxiom owlDisjointClassesAxiom) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyDomainAxiom owlDataPropertyDomainAxiom) {
        icon = dataPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLDisjointUnionAxiom owlDisjointUnionAxiom) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLSymmetricObjectPropertyAxiom owlSymmetricObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyRangeAxiom owlDataPropertyRangeAxiom) {
        icon = dataPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLFunctionalDataPropertyAxiom owlFunctionalDataPropertyAxiom) {
        icon = dataPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLEquivalentDataPropertiesAxiom owlEquivalentDataPropertiesAxiom) {
        icon = dataPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLEquivalentClassesAxiom owlEquivalentClassesAxiom) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLDataPropertyAssertionAxiom owlDataPropertyAssertionAxiom) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLTransitiveObjectPropertyAxiom owlTransitiveObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom owlIrreflexiveObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLSubDataPropertyOfAxiom owlDataSubPropertyAxiom) {
        icon = dataPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLSameIndividualAxiom owlSameIndividualsAxiom) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLClassAssertionAxiom owlClassAssertionAxiom) {
        icon = individualIcon;
    }

    @Override
    public void visit(@Nonnull OWLSubPropertyChainOfAxiom axiom) {
        icon = objectPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLClass owlClass) {
        if(definedClassChecker.isDefinedClass(owlClass)) {
            icon = definedClassIcon;
        }
        else {
            icon = primitiveClassIcon;
        }
    }

    @Override
    public void visit(@Nonnull OWLObjectOneOf owlEnumeration) {
        icon = primitiveClassIcon;
    }

    @Override
    public void visit(@Nonnull OWLAnnotationProperty owlAnnotationProperty) {
        icon = annotationPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom) {
        icon = annotationPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLSubAnnotationPropertyOfAxiom owlSubAnnotationPropertyOfAxiom) {
        icon = annotationPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLAnnotationPropertyDomainAxiom owlAnnotationPropertyDomainAxiom) {
        icon = annotationPropertyIcon;
    }

    @Override
    public void visit(@Nonnull OWLAnnotationPropertyRangeAxiom owlAnnotationPropertyRangeAxiom) {
        icon = annotationPropertyIcon;
    }
}
