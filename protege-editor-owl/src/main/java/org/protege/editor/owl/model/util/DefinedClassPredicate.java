package org.protege.editor.owl.model.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

import com.google.common.collect.ImmutableSet;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 19 Sep 16
 */
public class DefinedClassPredicate implements Predicate<OWLClass> {

    private final Set<OWLOntology> ontologies;

    /**
     * Constructs a DefinedClassPredicate that will test whether a class is defined in the specified set of ontologies.
     * A class is considered to be defined if it is an operand in an equivalent classes axiom in one of the listed
     * ontologies or if it is the subject of a DisjointUnion axiom in one of the listed ontologies.
     * @param ontologies The ontologies to be checked for class definition axioms.
     */
    public DefinedClassPredicate(@Nonnull Set<OWLOntology> ontologies) {
        this.ontologies = ImmutableSet.copyOf(checkNotNull(ontologies));
    }

    @Override
    public boolean test(@Nonnull OWLClass c) {
        checkNotNull(c);
        for(OWLOntology ontology : ontologies) {
            if(!ontology.getEquivalentClassesAxioms(c).isEmpty()) {
                return true;
            }
            if(!ontology.getDisjointUnionAxioms(c).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * A convenience factory method that creates a new DefinedClassPredicate for the specified set of ontologies.
     * @param ontologies The ontologies.
     * @return The DefinedClassPredicate.
     */
    @Nonnull
    public static Predicate<OWLClass> isDefinedIn(@Nonnull Set<OWLOntology> ontologies) {
        return new DefinedClassPredicate(ontologies);
    }

    /**
     * A convenience factory method that creates the negation of a new DefinedClassPredicate for the specified set of ontologies.
     * @param ontologies The ontologies.
     * @return The negation of a DefinedClassPredicate.
     */
    public static Predicate<OWLClass> isNotDefinedIn(@Nonnull Set<OWLOntology> ontologies) {
        return isDefinedIn(ontologies).negate();
    }
}
