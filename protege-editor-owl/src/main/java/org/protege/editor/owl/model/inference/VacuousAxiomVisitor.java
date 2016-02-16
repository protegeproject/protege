package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;


/**
 * This class performs simple heuristics to detect vacuous axioms.
 * <p/>
 * The motivation for this class is that certain inference processes will generate vacuous axioms as entailments.  The vacuous axioms 
 * are distracting and do not provide any addition information.  
 * 
 * @author tredmond
 *
 */
public class VacuousAxiomVisitor implements OWLAxiomVisitorEx<Boolean> {	
	private OWLDataFactory factory;
	

	public static boolean isVacuousAxiom(OWLAxiom axiom) {
		return axiom.accept(new VacuousAxiomVisitor());
	}
	
	/**
	 * Detect axioms that say that p is equivalent to inverse q or say that p inverseof inverse(q).
	 * 
	 * These are not vacuous axioms but they are axioms that we might want to exclude from the inferred output.  The idea is that
	 * there are two ways of indicating that p and q are inverse properties of one another and we want the inferred output to only contain 
	 * one of the cases.
	 * 
	 * @param axiom
	 */
	public static boolean involvesInverseSquared(OWLAxiom axiom) {
		if (axiom instanceof OWLEquivalentObjectPropertiesAxiom) {
			OWLEquivalentObjectPropertiesAxiom equivalentObjectProperties = (OWLEquivalentObjectPropertiesAxiom) axiom;
			boolean inverseFound = false;
			boolean namedFound = false;
			if (equivalentObjectProperties.getProperties().size() == 2) {
				for (OWLObjectPropertyExpression p : equivalentObjectProperties.getProperties()) {
					if (p.isAnonymous()) {
						inverseFound = true;
					}
					else {
						namedFound = true;
					}
				}
				return inverseFound && namedFound;
			}
		}
		if (axiom instanceof OWLInverseObjectPropertiesAxiom) {
			OWLInverseObjectPropertiesAxiom inverseObjectProperties = (OWLInverseObjectPropertiesAxiom) axiom;
			return inverseObjectProperties.getFirstProperty().isAnonymous() ^ inverseObjectProperties.getSecondProperty().isAnonymous();
		}
		return false;
	}

	public VacuousAxiomVisitor() {
		factory = OWLManager.getOWLDataFactory();
	}
	
	@Override
	public Boolean visit(OWLSubClassOfAxiom axiom) {
		return axiom.getSuperClass().equals(factory.getOWLThing()) || axiom.getSubClass().equals(factory.getOWLNothing());
	}



	@Override
	public Boolean visit(OWLDisjointClassesAxiom axiom) {
		return axiom.getClassExpressions().size() == 2 && axiom.getClassExpressions().contains(factory.getOWLNothing());
	}

	@Override
	public Boolean visit(OWLDataPropertyDomainAxiom axiom) {
		return axiom.getDomain().equals(factory.getOWLThing());
	}

	@Override
	public Boolean visit(OWLDataPropertyRangeAxiom axiom) {
		return axiom.getRange().equals(factory.getTopDatatype());
	}

	@Override
	public Boolean visit(OWLObjectPropertyDomainAxiom axiom) {
		return axiom.getDomain().equals(factory.getOWLThing());
	}

	@Override
	public Boolean visit(OWLObjectPropertyRangeAxiom axiom) {
		return axiom.getRange().equals(factory.getOWLThing());
	}

	@Override
	public Boolean visit(OWLDisjointDataPropertiesAxiom axiom) {
		return axiom.getProperties().size() == 2 && axiom.getProperties().contains(factory.getOWLBottomDataProperty());
	}

	@Override
	public Boolean visit(OWLDisjointObjectPropertiesAxiom axiom) {
		return axiom.getProperties().size() == 2 && axiom.getProperties().contains(factory.getOWLBottomObjectProperty());
	}

	@Override
	public Boolean visit(OWLSubObjectPropertyOfAxiom axiom) {
		return axiom.getSuperProperty().equals(factory.getOWLTopObjectProperty()) || axiom.getSubProperty().equals(factory.getOWLBottomObjectProperty());
	}

	@Override
	public Boolean visit(OWLClassAssertionAxiom axiom) {
		return axiom.getClassExpression().equals(factory.getOWLThing());
	}

	@Override
	public Boolean visit(OWLSubDataPropertyOfAxiom axiom) {
		return axiom.getSuperProperty().equals(factory.getOWLTopDataProperty()) || axiom.getSubProperty().equals(factory.getOWLBottomDataProperty());
	}

	@Override
	public Boolean visit(OWLObjectPropertyAssertionAxiom axiom) {
		return axiom.getProperty().equals(factory.getOWLTopObjectProperty());
	}
	
	@Override
	public Boolean visit(OWLDataPropertyAssertionAxiom axiom) {
		return axiom.getProperty().equals(factory.getOWLTopDataProperty());
	}
	
	// non-logical axioms cannot be inferred.
	
	@Override
	public Boolean visit(OWLDeclarationAxiom axiom) {
		return true;
	}

	@Override
	public Boolean visit(OWLAnnotationAssertionAxiom axiom) {
		return true;
	}
	
	@Override
	public Boolean visit(OWLSubAnnotationPropertyOfAxiom axiom) {
		return true;
	}

	@Override
	public Boolean visit(OWLAnnotationPropertyDomainAxiom axiom) {
		return true;
	}

	@Override
	public Boolean visit(OWLAnnotationPropertyRangeAxiom axiom) {
		return true;
	}
	
	/*
	 * Returning false is safe.
	 */
	

	@Override
	public Boolean visit(OWLInverseObjectPropertiesAxiom axiom) {
		return false;
	}
	

	@Override
	public Boolean visit(OWLEquivalentObjectPropertiesAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLDifferentIndividualsAxiom axiom) {
		return false;
	}


	@Override
	public Boolean visit(OWLFunctionalObjectPropertyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLEquivalentDataPropertiesAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLDisjointUnionAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLSymmetricObjectPropertyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLFunctionalDataPropertyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLEquivalentClassesAxiom axiom) {
		return false;
	}



	@Override
	public Boolean visit(OWLTransitiveObjectPropertyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLSameIndividualAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLSubPropertyChainOfAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLAsymmetricObjectPropertyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLReflexiveObjectPropertyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLHasKeyAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(OWLDatatypeDefinitionAxiom axiom) {
		return false;
	}

	@Override
	public Boolean visit(SWRLRule rule) {
		return false;
	}
	


}
