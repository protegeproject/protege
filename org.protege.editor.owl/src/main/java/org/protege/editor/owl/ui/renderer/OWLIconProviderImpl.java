package org.protege.editor.owl.ui.renderer;

import java.util.Set;

import javax.swing.Icon;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLAsymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataExactCardinality;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataMaxCardinality;
import org.semanticweb.owlapi.model.OWLDataMinCardinality;
import org.semanticweb.owlapi.model.OWLDataOneOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLHasKeyAxiom;
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectExactCardinality;
import org.semanticweb.owlapi.model.OWLObjectHasSelf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLObjectVisitorAdapter;


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

    private final Icon primitiveClassIcon = new OWLClassIcon(OWLClassIcon.Type.PRIMITIVE);//OWLIcons.getIcon("class.primitive.png");

    private final Icon definedClassIcon = new OWLClassIcon(OWLClassIcon.Type.DEFINED);

    private final Icon objectPropertyIcon = new OWLObjectPropertyIcon();

    private final Icon dataPropertyIcon = new OWLDataPropertyIcon();

    private final Icon annotationPropertyIcon = new OWLAnnotationPropertyIcon();

    private final Icon individualIcon = new OWLIndividualIcon();

    private final Icon dataTypeIcon = OWLIcons.getIcon("datarange.png");

    private final Icon ontologyIcon = OWLIcons.getIcon("ontology.png");

    private final Icon ontologyMissing = OWLIcons.getIcon("ontology.missing.png");


    private OWLModelManager owlModelManager;


    public OWLIconProviderImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public Icon getIcon() {
        return icon;
    }


    @Override
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
    public void visit(OWLObjectIntersectionOf owlAnd) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDatatype owlDatatype) {
        icon = dataTypeIcon;
    }


    @Override
    public void visit(OWLDataOneOf owlDataEnumeration) {
        icon = dataTypeIcon;
    }


    @Override
    public void visit(OWLDataAllValuesFrom owlDataAllRestriction) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDataProperty owlDataProperty) {
        icon = dataPropertyIcon;
    }


    @Override
    public void visit(OWLDataSomeValuesFrom owlDataSomeValuesFrom) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDataHasValue owlDataValueRestriction) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDifferentIndividualsAxiom owlDifferentIndividualsAxiom) {
        icon = individualIcon;
    }


    @Override
    public void visit(OWLDisjointDataPropertiesAxiom owlDisjointDataPropertiesAxiom) {
        icon = dataPropertyIcon;
    }


    @Override
    public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLHasKeyAxiom owlHasKeyAxiom) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDatatypeDefinitionAxiom owlDatatypeDefinitionAxiom) {
        icon = dataTypeIcon;
    }


    @Override
    public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLObjectPropertyDomainAxiom axiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLEquivalentObjectPropertiesAxiom owlEquivalentObjectPropertiesAxiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLNegativeDataPropertyAssertionAxiom owlNegativeDataPropertyAssertionAxiom) {
        icon = individualIcon;
    }


    @Override
    public void visit(OWLObjectPropertyRangeAxiom axiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLObjectPropertyAssertionAxiom owlObjectPropertyAssertionAxiom) {
        icon = individualIcon;
    }


    @Override
    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLNamedIndividual owlIndividual) {
        icon = individualIcon;
    }


    @Override
    public void visit(OWLAnonymousIndividual individual) {
        icon = individualIcon;
    }


    @Override
    public void visit(OWLObjectAllValuesFrom owlObjectAllRestriction) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLObjectMinCardinality desc) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLObjectExactCardinality desc) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLObjectMaxCardinality desc) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLObjectHasSelf desc) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDataMinCardinality desc) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDataExactCardinality desc) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDataMaxCardinality desc) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLObjectProperty owlObjectProperty) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLObjectSomeValuesFrom owlObjectSomeValuesFrom) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLObjectHasValue owlObjectValueRestriction) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLObjectComplementOf owlNot) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLOntology owlOntology) {
        icon = ontologyIcon;
    }


    @Override
    public void visit(OWLObjectUnionOf owlOr) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDeclarationAxiom owlDeclarationAxiom) {
        owlDeclarationAxiom.getEntity().accept(this);
    }


    @Override
    public void visit(OWLSubClassOfAxiom owlSubClassAxiom) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLNegativeObjectPropertyAssertionAxiom owlNegativeObjectPropertyAssertionAxiom) {
        icon = individualIcon;
    }


    @Override
    public void visit(OWLAsymmetricObjectPropertyAxiom owlAntiSymmetricObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLReflexiveObjectPropertyAxiom owlReflexiveObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLDisjointClassesAxiom owlDisjointClassesAxiom) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDataPropertyDomainAxiom owlDataPropertyDomainAxiom) {
        icon = dataPropertyIcon;
    }


    @Override
    public void visit(OWLDisjointUnionAxiom owlDisjointUnionAxiom) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLSymmetricObjectPropertyAxiom owlSymmetricObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLDataPropertyRangeAxiom owlDataPropertyRangeAxiom) {
        icon = dataPropertyIcon;
    }


    @Override
    public void visit(OWLFunctionalDataPropertyAxiom owlFunctionalDataPropertyAxiom) {
        icon = dataPropertyIcon;
    }


    @Override
    public void visit(OWLEquivalentDataPropertiesAxiom owlEquivalentDataPropertiesAxiom) {
        icon = dataPropertyIcon;
    }


    @Override
    public void visit(OWLEquivalentClassesAxiom owlEquivalentClassesAxiom) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLDataPropertyAssertionAxiom owlDataPropertyAssertionAxiom) {
        icon = individualIcon;
    }


    @Override
    public void visit(OWLTransitiveObjectPropertyAxiom owlTransitiveObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLIrreflexiveObjectPropertyAxiom owlIrreflexiveObjectPropertyAxiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLSubDataPropertyOfAxiom owlDataSubPropertyAxiom) {
        icon = dataPropertyIcon;
    }


    @Override
    public void visit(OWLSameIndividualAxiom owlSameIndividualsAxiom) {
        icon = individualIcon;
    }

    
    @Override
    public void visit(OWLClassAssertionAxiom owlClassAssertionAxiom) {
        icon = individualIcon;
    }


    @Override
    public void visit(OWLSubPropertyChainOfAxiom axiom) {
        icon = objectPropertyIcon;
    }


    @Override
    public void visit(OWLClass owlClass) {
        for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
            if (isDefined(owlClass, ont)) {
                icon = definedClassIcon;
                return;
            }
        }
        icon = primitiveClassIcon;
    }
    
    private boolean isDefined(OWLClass owlClass, OWLOntology ontology) {
        if (EntitySearcher.isDefined(owlClass, ontology)) {
    		return true;
    	}
    	Set<OWLDisjointUnionAxiom> axioms = ontology.getDisjointUnionAxioms(owlClass);
    	return axioms != null && !axioms.isEmpty();
    }


    @Override
    public void visit(OWLObjectOneOf owlEnumeration) {
        icon = primitiveClassIcon;
    }


    @Override
    public void visit(OWLAnnotationProperty owlAnnotationProperty) {
        icon = annotationPropertyIcon;
    }


    @Override
    public void visit(OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom) {
        icon = annotationPropertyIcon;
    }


    @Override
    public void visit(OWLSubAnnotationPropertyOfAxiom owlSubAnnotationPropertyOfAxiom) {
        icon = annotationPropertyIcon;
    }


    @Override
    public void visit(OWLAnnotationPropertyDomainAxiom owlAnnotationPropertyDomainAxiom) {
        icon = annotationPropertyIcon;
    }


    @Override
    public void visit(OWLAnnotationPropertyRangeAxiom owlAnnotationPropertyRangeAxiom) {
        icon = annotationPropertyIcon;
    }
}
