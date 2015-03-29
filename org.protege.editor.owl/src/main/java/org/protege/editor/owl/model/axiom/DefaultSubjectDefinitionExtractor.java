package org.protege.editor.owl.model.axiom;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

import com.google.common.collect.Sets;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class DefaultSubjectDefinitionExtractor implements SubjectDefinitionExtractor {

    @Override
    public Set<OWLAxiom> getDefiningAxioms(final OWLObject subject, final OWLOntology ontology) {
            return new HashSet<>(subject.accept(new OWLObjectVisitorExAdapter<Set<? extends OWLAxiom>>(Collections.<OWLAxiom>emptySet()) {

                @Override
                public Set<? extends OWLAxiom> visit(OWLClass cls) {
                    return ontology.getAxioms(cls);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLObjectProperty property) {
                    return ontology.getAxioms(property);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLDataProperty property) {
                    return ontology.getAxioms(property);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLNamedIndividual individual) {
                    return ontology.getAxioms(individual);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLDatatype datatype) {
                    return ontology.getAxioms(datatype);
                }

                @Override
                public Set<? extends OWLAxiom> visit(OWLAnnotationProperty property) {
                    return ontology.getAxioms(property);
                }

                @Override
                public Set<? extends OWLAxiom> visit(IRI iri) {
                    Set<OWLAxiom> axioms = Sets.newHashSet();
                    axioms.addAll(ontology.getAnnotationAssertionAxioms(iri));
                    for(OWLEntity entity : ontology.getEntitiesInSignature(iri, true)) {
                        axioms.addAll(getDefiningAxioms(entity, ontology));
                    }
                    return axioms;
                }
            }));
    }
}
