package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectVisitorExAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/03/15
 */
public class OWLObjectRemover {

    /**
     * Gets the changes to remove the specified entity from the specified ontology.
     *
     * @param object  The entity.  Not {@code null}.
     * @return The changes.
     */
    public List<OWLOntologyChange> getChangesToRemoveObject(OWLEntity object, final OWLOntology ontology) {
        return getChangesToRemoveObject((OWLObject) object, ontology);
    }



    /**
     * Gets the changes to remove the specified individual from the specified ontology.
     *
     * @param object  The individual.  Not {@code null}.
     * @return The changes.
     */
    public List<OWLOntologyChange> getChangesToRemoveObject(OWLAnonymousIndividual object, final OWLOntology ontology) {
        return getChangesToRemoveObject((OWLObject) object, ontology);
    }


    private List<OWLOntologyChange> getChangesToRemoveObject(OWLObject object, final OWLOntology ontology) {
        return object.accept(new OWLObjectVisitorExAdapter<List<OWLOntologyChange>>() {
            @Override
            public List<OWLOntologyChange> visit(OWLDatatype datatype) {
                return getChangesForEntity(datatype, ontology);
            }

            @Override
            public List<OWLOntologyChange> visit(OWLDataProperty property) {
                return getChangesForEntity(property, ontology);
            }

            @Override
            public List<OWLOntologyChange> visit(OWLObjectProperty property) {
                return getChangesForEntity(property, ontology);
            }

            @Override
            public List<OWLOntologyChange> visit(OWLNamedIndividual individual) {
                return getChangesForEntity(individual, ontology);
            }

            @Override
            public List<OWLOntologyChange> visit(OWLClass desc) {
                return getChangesForEntity(desc, ontology);
            }

            @Override
            public List<OWLOntologyChange> visit(OWLAnonymousIndividual individual) {
                return getChangesForAnonymousIndividual(individual, ontology);
            }

            @Override
            public List<OWLOntologyChange> visit(OWLAnnotationProperty property) {
                List<OWLOntologyChange> changes = getChangesForEntity(property, ontology);
                changes.addAll(getChangesForOntologyAnnotations(property, ontology));
                return changes;
            }
        });
    }

    private List<OWLOntologyChange> getChangesForAnonymousIndividual(OWLAnonymousIndividual individual, OWLOntology ont) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLAxiom ax : ont.getReferencingAxioms(individual)) {
            changes.add(new RemoveAxiom(ont, ax));
        }
        changes.addAll(getChangesForAnnotationSubject(individual, ont));
        changes.addAll(getChangesForAnnotationValue(individual, ont));
        return changes;
    }

    private List<OWLOntologyChange> getChangesForEntity(OWLEntity entity, OWLOntology ont) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLAxiom ax : ont.getReferencingAxioms(entity)) {
            changes.add(new RemoveAxiom(ont, ax));
        }
        IRI entityIRI = entity.getIRI();
        changes.addAll(getChangesForAnnotationSubject(entityIRI, ont));
        changes.addAll(getChangesForAnnotationValue(entityIRI, ont));
        changes.addAll(getChangesForOntologyAnnotations(entityIRI, ont));
        return changes;
    }


    private List<OWLOntologyChange> getChangesForAnnotationSubject(OWLAnnotationSubject subject, OWLOntology ont) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLAnnotationAssertionAxiom ax : ont.getAnnotationAssertionAxioms(subject)) {
            changes.add(new RemoveAxiom(ont, ax));
        }
        return changes;
    }

    private List<OWLOntologyChange> getChangesForAnnotationValue(OWLAnnotationValue object, OWLOntology ont) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLAnnotationAssertionAxiom ax : ont.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
            if (ax.getValue().equals(object)) {
                changes.add(new RemoveAxiom(ont, ax));
            }
        }
        return changes;
    }

    private List<OWLOntologyChange> getChangesForOntologyAnnotations(OWLAnnotationProperty annotationProperty, OWLOntology ont) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLAnnotation annotation : ont.getAnnotations()) {
            if (annotation.getProperty().equals(annotationProperty)) {
                changes.add(new RemoveOntologyAnnotation(ont, annotation));
            }
        }
        return changes;
    }

    private List<OWLOntologyChange> getChangesForOntologyAnnotations(OWLAnnotationValue value, OWLOntology ont) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        for (OWLAnnotation annotation : ont.getAnnotations()) {
            if (annotation.getValue().equals(value)) {
                changes.add(new RemoveOntologyAnnotation(ont, annotation));
            }
        }
        return changes;
    }
}
