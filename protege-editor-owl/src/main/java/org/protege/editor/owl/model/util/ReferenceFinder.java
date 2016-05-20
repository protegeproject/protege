package org.protege.editor.owl.model.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;

import java.util.Collection;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20 May 16
 */
public class ReferenceFinder {

    /**
     * Gets the references set for the specified entities in the specified ontology.
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

        Multimap<IRI, OWLAnnotationAssertionAxiom> annotationAssertionsByIri = HashMultimap.create();
        Multimap<IRI, OWLAnnotation> ontologyAnnotationsByIri = HashMultimap.create();
        Multimap<OWLEntity, OWLAnnotation> ontologyAnnotationsByProperty = HashMultimap.create();

        buildIndex(ontology,
                annotationAssertionsByIri,
                ontologyAnnotationsByIri,
                ontologyAnnotationsByProperty);

        for(OWLEntity entity : entities) {
            Set<OWLAxiom> refs = ontology.getReferencingAxioms(entity, Imports.EXCLUDED);
            axiomSetBuilder.addAll(refs);
            axiomSetBuilder.addAll(annotationAssertionsByIri.get(entity.getIRI()));
            ontologyAnnotationSetBuilder.addAll(ontologyAnnotationsByIri.get(entity.getIRI()));
            ontologyAnnotationSetBuilder.addAll(ontologyAnnotationsByProperty.get(entity));
        }

        return new ReferenceSet(
                ontology,
                axiomSetBuilder.build(),
                ontologyAnnotationSetBuilder.build()
        );

    }

    private static void buildIndex(OWLOntology ontology,
                                   Multimap<IRI, OWLAnnotationAssertionAxiom> annotationAssertionsByIri,
                                   Multimap<IRI, OWLAnnotation> ontologyAnnotationsByIri,
                                   Multimap<OWLEntity, OWLAnnotation> ontologyAnnotationsByProperty) {

        for(OWLAnnotationAssertionAxiom axiom : ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
            OWLAnnotationSubject subject = axiom.getSubject();
            if(subject instanceof IRI) {
                annotationAssertionsByIri.put((IRI) subject, axiom);
            }
            OWLAnnotationValue value = axiom.getValue();
            if(value instanceof IRI) {
                annotationAssertionsByIri.put((IRI) value, axiom);
            }
        }

        for(OWLAnnotation annotation : ontology.getAnnotations()) {
            OWLAnnotationValue value = annotation.getValue();
            if(value instanceof IRI) {
                ontologyAnnotationsByIri.put((IRI) value, annotation);
            }
            ontologyAnnotationsByProperty.put(annotation.getProperty(), annotation);
        }
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
