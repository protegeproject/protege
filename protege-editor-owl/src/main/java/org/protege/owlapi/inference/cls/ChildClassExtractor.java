package org.protege.owlapi.inference.cls;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import com.google.common.collect.ImmutableSet;


public class ChildClassExtractor extends OWLAxiomVisitorAdapter {


    private NamedConjunctChecker checker = new NamedConjunctChecker();

    private NamedClassExtractor namedClassExtractor = new NamedClassExtractor();

    @Nullable
    private OWLClass currentParentClass;

    private ImmutableSet<OWLObjectProperty> relationships = ImmutableSet.of();

    private Set<OWLClass> results = new HashSet<>();

    private Map<OWLClass, OWLObjectPropertyExpression> child2RelationshipMap = new HashMap<>();


    @Deprecated
    public void reset() {
        clear();
    }


    /**
     * Sets the parent class whose children are to be extracted.
     * @param currentParentClass The parent class.
     */
    public void setCurrentParentClass(@Nonnull OWLClass currentParentClass) {
        this.currentParentClass = checkNotNull(currentParentClass);
        clear();
    }


    @Nonnull
    public Optional<OWLClass> getCurrentParentClass() {
        return Optional.ofNullable(currentParentClass);
    }

    private void clear() {
        results.clear();
        namedClassExtractor.reset();
        child2RelationshipMap.clear();
    }

    public Collection<OWLClass> getResult() {
        return new ArrayList<>(results);
    }


    public void visit(OWLSubClassOfAxiom axiom) {
        if (axiom.getSubClass().isAnonymous()) {
            // Not in our results because we only want to return class names
            return;
        }
        // Example:
        // If searching for subs of B, candidates are:
        // SubClassOf(A B)
        // SubClassOf(A And(B ...))
        if (checker.containsConjunct(currentParentClass, axiom.getSuperClass())) {
            results.add(axiom.getSubClass().asOWLClass());
        }
        else if (!relationships.isEmpty()) {
            // SubClassOf(A ObjectSomeValuesFrom(p B))
            axiom.getSuperClass().asConjunctSet().stream()
                 .filter(ce -> ce instanceof OWLObjectSomeValuesFrom)
                 .map(ce -> ((OWLObjectSomeValuesFrom) ce))
                 .filter(svf -> !svf.getProperty().isAnonymous())
                 .filter(svf -> svf.getFiller().equals(currentParentClass))
                 .filter(svf -> relationships.contains(svf.getProperty().asOWLObjectProperty()))
                 .findFirst().ifPresent(c -> {
                OWLClass child = axiom.getSubClass().asOWLClass();
                results.add(child);
                child2RelationshipMap.put(child, c.getProperty());
            });
        }
    }


    public void visit(OWLEquivalentClassesAxiom axiom) {
        // EquivalentClasses(A  And(B...))
        if (!namedClassInEquivalentAxiom(axiom)) {
            return;
        }
        Set<OWLClassExpression> candidateDescriptions = new HashSet<>();
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

    public void setRelationshipProperties(ImmutableSet<OWLObjectProperty> properties) {
        this.relationships = properties;
    }

    public Set<OWLObjectProperty> getRelationships() {
        return relationships;
    }

    public Optional<OWLObjectPropertyExpression> getRelationship(OWLClass child) {
        return Optional.ofNullable(child2RelationshipMap.get(child));
    }

    private boolean namedClassInEquivalentAxiom(OWLEquivalentClassesAxiom axiom) {
        for (OWLClassExpression equiv : axiom.getClassExpressions()) {
            if (!equiv.isAnonymous()) {
                return true;
            }
        }
        return false;
    }
}