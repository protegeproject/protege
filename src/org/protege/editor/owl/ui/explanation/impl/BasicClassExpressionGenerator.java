package org.protege.editor.owl.ui.explanation.impl;

import java.util.UUID;

import org.semanticweb.owlapi.debugging.DebuggerClassExpressionGenerator;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

public class BasicClassExpressionGenerator extends DebuggerClassExpressionGenerator {
	private OWLDataFactory factory;
    private OWLClassExpression desc;
    
	
	public BasicClassExpressionGenerator(OWLDataFactory factory) {
		super(factory);
		this.factory = factory;
	}
	
    public OWLClassExpression getDebuggerClassExpression() {
    	if (desc != null) {
    		return desc;
    	}
    	else {
    		return super.getDebuggerClassExpression();
    	}
    }
    
	protected OWLDataFactory getOWLDataFactory() {
		return factory;
	}
	
	protected OWLNamedIndividual getSkolemVariable() {
		return factory.getOWLNamedIndividual(IRI.create("http://debug.com/#" + UUID.randomUUID().toString().replace('-', '_')));
	}
	
    public void visit(OWLInverseObjectPropertiesAxiom axiom) {
    	OWLObjectPropertyExpression p = axiom.getFirstProperty();
    	OWLObjectPropertyExpression q = factory.getOWLObjectInverseOf(axiom.getSecondProperty()); // p and q are equivalent?
    	OWLNamedIndividual i = getSkolemVariable();
    	
    	OWLClassExpression someP = factory.getOWLObjectHasValue(p, i);
    	OWLClassExpression someQ = factory.getOWLObjectHasValue(q, i);
    	desc= factory.getOWLObjectUnionOf(
    			factory.getOWLObjectIntersectionOf(someP, factory.getOWLObjectComplementOf(someQ)),
    			factory.getOWLObjectIntersectionOf(someQ, factory.getOWLObjectComplementOf(someP))
    	);
    	
    }
    
}
