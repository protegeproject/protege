package org.protege.editor.owl.model.search.lucene;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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

    private Set<OWLEntity> removeDeclarations = new HashSet<>();
    private Multimap<OWLEntity, OWLAnnotation> removeAnnotations = ArrayListMultimap.create();

    private Set<OWLEntity> addDeclarations = new HashSet<>();
    private Multimap<OWLEntity, OWLAnnotation> addAnnotations = ArrayListMultimap.create();

    public ChangeSet() {
        // NO-OP
    }

    /**
     * Get a set of entities that has been removed from the ontology.
     *
     * @return An unmodified set of removed entities.
     */
    public Set<OWLEntity> getRemoveDeclarations() {
        return Collections.unmodifiableSet(removeDeclarations);
    }

    /**
     * Collect the removed entities from an ontology. Hint: Use {@link OWLOntologyChange} to
     * know what declaration axioms that have been removed and get the subject entity.
     *
     * @param entity
     *          The removed entity.
     */
    public void addRemoveDeclaration(OWLEntity entity) {
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

    /**
     * Collect the removed annotations from an ontology. Hint: Use {@link OWLOntologyChange} to
     * know what annotation assertion axioms that have been removed from the ontology. The method
     * requires to include the subject entity of the annotation assertion.
     *
     * @param subject
     *          The subject entity of the annotation assertion.
     * @param annotation
     *          The annotation object.
     */
    public void addRemoveAnnotation(OWLEntity subject, OWLAnnotation annotation) {
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

    /**
     * Collect the added entities to an ontology.  Hint: Use {@link OWLOntologyChange} to
     * know what declaration axioms that have been added and get the subject entity.
     *
     * @param entity
     *          The added entity.
     */
    public void addAddDeclaration(OWLEntity entity) {
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

    /**
     * Collect the added annotations to an ontology. Hint: Use {@link OWLOntologyChange} to
     * know what annotation assertion axioms that have been added to the ontology. The method
     * requires to include the subject entity of the annotation assertion.
     *
     * @param subject
     *          The subject entity of the annotation assertion.
     * @param annotation
     *          The annotation object.
     */
    public void addAddAnnotation(OWLEntity subject, OWLAnnotation annotation) {
        addAnnotations.put(subject, annotation);
    }
}
