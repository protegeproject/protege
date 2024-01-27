package org.protege.editor.owl.model.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 May 16
 */
public class ReferenceFinder {

    /**
     * Gets the references set for the specified entities in the specified ontology.
     *
     * @param entities The entities whose references are to be retrieved. Not {@code null}.
     * @param ontology The ontology.  Not {@code null}.
     * @return The ReferenceSet that contains axioms that reference the specified entities and ontology annotations
     * that reference the specified entities.  Note that, since annotation assertions have subjects that may be IRIs and
     * values that may be IRIs, and ontology annotation have values that may be IRIs, the reference set includes these
     * axioms where the IRI is the IRI of one or more of the specified entities.
     */
    public ReferenceSet getReferenceSet(
            Collection<? extends OWLEntity> entities,
            OWLOntology ontology) {


        ImmutableSet.Builder<OWLAxiom> axiomSetBuilder = ImmutableSet.builder();

        ImmutableSet.Builder<OWLAnnotation> ontologyAnnotationSetBuilder = ImmutableSet.builder();


        Set<IRI> entityIRIs = new HashSet<>(entities.size());
        for (OWLEntity entity : entities) {
            Set<OWLAxiom> refs = ontology.getReferencingAxioms(entity, Imports.EXCLUDED);
            axiomSetBuilder.addAll(refs);
            entityIRIs.add(entity.getIRI());
        }

        // Optimisation for the case where there is just one entity.
        if(entityIRIs.size() == 1) {
            entityIRIs = Collections.singleton(entityIRIs.iterator().next());
        }

        for (OWLAnnotationAssertionAxiom axiom : ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
            OWLAnnotationSubject subject = axiom.getSubject();
            if (subject instanceof IRI && entityIRIs.contains(subject)) {
                axiomSetBuilder.add(axiom);
            }
            else {
                OWLAnnotationValue value = axiom.getValue();
                if (value instanceof IRI && entityIRIs.contains(value)) {
                    axiomSetBuilder.add(axiom);
                }
            }
        }

        for (OWLAnnotation annotation : ontology.getAnnotations()) {
            OWLAnnotationValue value = annotation.getValue();
            if (value instanceof IRI && entityIRIs.contains(value)) {
                ontologyAnnotationSetBuilder.add(annotation);
            }
            else {
                if (entities.contains(annotation.getProperty())) {
                    ontologyAnnotationSetBuilder.add(annotation);
                }
            }
        }

        return new ReferenceSet(
                ontology,
                axiomSetBuilder.build(),
                ontologyAnnotationSetBuilder.build()
        );

    }


    public static final class ReferenceSet {

        private final OWLOntology ontology;

        private final ImmutableCollection<OWLAxiom> referencingAxioms;

        private final ImmutableCollection<OWLAnnotation> referencingOntologyAnnotations;

        public ReferenceSet(OWLOntology ontology, ImmutableCollection<OWLAxiom> referencingAxioms, ImmutableCollection<OWLAnnotation> referencingOntologyAnnotations) {
            this.ontology = checkNotNull(ontology);
            this.referencingAxioms = checkNotNull(referencingAxioms);
            this.referencingOntologyAnnotations = checkNotNull(referencingOntologyAnnotations);
        }

        public OWLOntology getOntology() {
            return ontology;
        }

        public ImmutableCollection<OWLAxiom> getReferencingAxioms() {
            return referencingAxioms;
        }

        public ImmutableCollection<OWLAnnotation> getReferencingOntologyAnnotations() {
            return referencingOntologyAnnotations;
        }
    }
}
