package org.protege.editor.owl.model.axiom;

import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class DefaultSubjectDefinitionExtractor implements SubjectDefinitionExtractor {

    @Override
    public Set<OWLAxiom> getDefiningAxioms(final OWLObject subject, final OWLOntology ontology) {
            return new HashSet<>(subject.accept(new OWLObjectVisitorEx<Set<? extends OWLAxiom>>() {

                @Override
                public Set<? extends OWLAxiom> visit(OWLClass cls) {
                    return ontology.getAxioms(cls, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLObjectProperty property) {
                    return ontology.getAxioms(property, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLDataProperty property) {
                    return ontology.getAxioms(property, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLNamedIndividual individual) {
                    return ontology.getAxioms(individual, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLDatatype datatype) {
                    return ontology.getAxioms(datatype, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLAnnotationProperty property) {
                    return ontology.getAxioms(property, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(IRI iri) {
                    Set<OWLAxiom> axioms = Sets.newHashSet();
                    axioms.addAll(ontology.getAnnotationAssertionAxioms(iri));
                    for(OWLEntity entity : ontology.getEntitiesInSignature(iri, Imports.INCLUDED)) {
                        axioms.addAll(getDefiningAxioms(entity, ontology));
                    }
                    return axioms;
                }
            }));
    }
}
