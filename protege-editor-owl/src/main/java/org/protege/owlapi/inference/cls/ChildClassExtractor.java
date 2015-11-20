package org.protege.owlapi.inference.cls;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import java.util.HashSet;
import java.util.Set;



public class ChildClassExtractor extends OWLAxiomVisitorAdapter {


    private NamedConjunctChecker checker = new NamedConjunctChecker();

    private NamedClassExtractor namedClassExtractor = new NamedClassExtractor();

    private OWLClass currentParentClass;

    private Set<OWLClass> results = new HashSet<OWLClass>();


    public void reset() {
        results.clear();
        namedClassExtractor.reset();
    }


    public void setCurrentParentClass(OWLClass currentParentClass) {
        this.currentParentClass = currentParentClass;
        reset();
    }


    public Set<OWLClass> getResult() {
        return new HashSet<OWLClass>(results);
    }


    public void visit(OWLSubClassOfAxiom axiom) {
        // Example:
        // If searching for subs of B, candidates are:
        // SubClassOf(A B)
        // SubClassOf(A And(B ...))
        if (checker.containsConjunct(currentParentClass, axiom.getSuperClass())) {
            // We only want named classes
            if (!axiom.getSubClass().isAnonymous()) {
                results.add(axiom.getSubClass().asOWLClass());
            }
        }
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        // EquivalentClasses(A  And(B...))
        if (!namedClassInEquivalentAxiom(axiom)){
            return;
        }
        Set<OWLClassExpression> candidateDescriptions = new HashSet<OWLClassExpression>();
        boolean found = false;
        for (OWLClassExpression equivalentClass : axiom.getClassExpressions()) {
            if (!checker.containsConjunct(currentParentClass, equivalentClass)) {
                // Potential operand
                candidateDescriptions.add(equivalentClass);
            }
            else {
                // This axiom is relevant
                if (equivalentClass.isAnonymous()) {
                    found = true;
                }
            }
        }
        if (!found) {
            return;
        }
        namedClassExtractor.reset();
        for (OWLClassExpression desc : candidateDescriptions) {
            desc.accept(namedClassExtractor);
        }
        results.addAll(namedClassExtractor.getResult());
    }


    private boolean namedClassInEquivalentAxiom(OWLEquivalentClassesAxiom axiom) {
        for (OWLClassExpression equiv : axiom.getClassExpressions()){
            if (!equiv.isAnonymous()){
                return true;
            }
        }
        return false;
    }
}