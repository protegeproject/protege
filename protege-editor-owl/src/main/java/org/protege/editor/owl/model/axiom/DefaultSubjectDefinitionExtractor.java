package org.protege.editor.owl.model.axiom;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import javax.annotation.Nonnull;
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
                public <T> Set<? extends OWLAxiom> doDefault(@Nonnull T object) {
                    return Collections.emptySet();
                }

                @Override
                public Set<? extends OWLAxiom> visit(@Nonnull OWLClass cls) {
                    return ontology.getAxioms(cls, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(@Nonnull OWLObjectProperty property) {
                    return ontology.getAxioms(property, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(@Nonnull OWLDataProperty property) {
                    return ontology.getAxioms(property, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(@Nonnull OWLNamedIndividual individual) {
                    return ontology.getAxioms(individual, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(@Nonnull OWLDatatype datatype) {
                    return ontology.getAxioms(datatype, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(@Nonnull OWLAnnotationProperty property) {
                    return ontology.getAxioms(property, Imports.EXCLUDED);
                }

                @Override
                public Set<? extends OWLAxiom> visit(@Nonnull IRI iri) {
                    Set<OWLAxiom> axioms = new HashSet<>(ontology.getAnnotationAssertionAxioms(iri));
                    for(OWLEntity entity : ontology.getEntitiesInSignature(iri, Imports.INCLUDED)) {
                        axioms.addAll(getDefiningAxioms(entity, ontology));
                    }
                    return axioms;
                }
            }));
    }
}
