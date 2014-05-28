package org.protege.editor.owl.model.axiom;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class DefaultSubjectDefinitionExtractor implements SubjectDefinitionExtractor {

    @Override
    public Set<OWLAxiom> getDefiningAxioms(final OWLObject subject, final OWLOntology ontology) {
            return new HashSet<>(subject.accept(new OWLObjectVisitorExAdapter<Set<? extends OWLAxiom>>() {
                @Override
                protected Set<? extends OWLAxiom> getDefaultReturnValue(OWLObject object) {
                    return Collections.emptySet();
                }

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
            }));
    }
}
