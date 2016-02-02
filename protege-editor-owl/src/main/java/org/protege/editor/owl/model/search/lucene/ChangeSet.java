package org.protege.editor.owl.model.search.lucene;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 27/01/2016
 */
public class ChangeSet {

    private List<? extends OWLOntologyChange> changes;

    private Set<OWLEntity> removeDeclarations = new HashSet<>();
    private Multimap<OWLEntity, OWLAnnotation> removeAnnotations = ArrayListMultimap.create();

    private Set<OWLEntity> addDeclarations = new HashSet<>();
    private Multimap<OWLEntity, OWLAnnotation> addAnnotations = ArrayListMultimap.create();

    public ChangeSet(List<? extends OWLOntologyChange> changes) {
        this.changes = changes;
        for (OWLOntologyChange change : changes) {
            if (!change.isAxiomChange()) continue; // ignore if it is not an axiom change
            OWLOntology sourceOntology = change.getOntology();
            OWLAxiom changedAxiom = change.getAxiom();
            if (change instanceof RemoveAxiom) {
                if (changedAxiom instanceof OWLDeclarationAxiom) {
                    OWLEntity entity = ((OWLDeclarationAxiom) changedAxiom).getEntity();
                    addRemoveDeclaration(entity);
                } else if (changedAxiom instanceof OWLAnnotationAssertionAxiom) {
                    OWLAnnotationSubject annotationSubject = ((OWLAnnotationAssertionAxiom) changedAxiom).getSubject();
                    if (annotationSubject instanceof IRI) {
                        Optional<OWLEntity> annotationEntitySubject = sourceOntology.getEntitiesInSignature((IRI) annotationSubject).stream().findFirst();
                        if (annotationEntitySubject.isPresent()) {
                            OWLAnnotation annotation = ((OWLAnnotationAssertionAxiom) changedAxiom).getAnnotation();
                            addRemoveAnnotation(annotationEntitySubject.get(), annotation);
                        }
                    }
                }
            } else if (change instanceof AddAxiom) {
                if (changedAxiom instanceof OWLDeclarationAxiom) {
                    OWLEntity entity = ((OWLDeclarationAxiom) changedAxiom).getEntity();
                    addAddDeclaration(entity);
                } else if (changedAxiom instanceof OWLAnnotationAssertionAxiom) {
                    OWLAnnotationSubject annotationSubject = ((OWLAnnotationAssertionAxiom) changedAxiom).getSubject();
                    if (annotationSubject instanceof IRI) {
                        Optional<OWLEntity> annotationEntitySubject = sourceOntology.getEntitiesInSignature((IRI) annotationSubject).stream().findFirst();
                        if (annotationEntitySubject.isPresent()) {
                            OWLAnnotation annotation = ((OWLAnnotationAssertionAxiom) changedAxiom).getAnnotation();
                            addAddAnnotation(annotationEntitySubject.get(), annotation);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the number of changes from ontology editing.
     *
     * @return Returns the number of changes from ontology editing.
     */
    public int size() {
        return changes.size();
    }

    /**
     * Get a set of entities that has been removed from the ontology.
     *
     * @return An unmodified set of removed entities.
     */
    public Set<OWLEntity> getRemoveDeclarations() {
        return Collections.unmodifiableSet(removeDeclarations);
    }

    protected void addRemoveDeclaration(OWLEntity entity) {
        removeDeclarations.add(entity);
    }

    /**
     * Get a collection of annotations that has been removed from the ontology. The annotation
     * comes as a pair where you can obtain the subject entity of the annotation assertion.
     *
     * @return An unmodified collection of subject entity and annotion pairs that have been
     *          removed from the ontology.
     */
    public Collection<Entry<OWLEntity, OWLAnnotation>> getRemoveAnnotations() {
        return Collections.unmodifiableCollection(removeAnnotations.entries());
    }

    protected void addRemoveAnnotation(OWLEntity subject, OWLAnnotation annotation) {
        removeAnnotations.put(subject, annotation);
    }

    /**
     * Get a set of entities that has been added to the ontology.
     *
     * @return An unmodifiable set of added entities.
     */
    public Set<OWLEntity> getAddDeclarations() {
        return Collections.unmodifiableSet(addDeclarations);
    }

    protected void addAddDeclaration(OWLEntity entity) {
        addDeclarations.add(entity);
    }

    /**
     * Get a collection annotations that has been added to the ontology. The annotation
     * comes as a pair where you can obtain the subject entity of the annotation assertion.
     *
     * @return An unmodified collection of subject entity and annotion pairs that have
     *          been added to the ontology.
     */
    public Collection<Entry<OWLEntity, OWLAnnotation>> getAddAnnotations() {
        return Collections.unmodifiableCollection(addAnnotations.entries());
    }

    protected void addAddAnnotation(OWLEntity subject, OWLAnnotation annotation) {
        addAnnotations.put(subject, annotation);
    }
}
