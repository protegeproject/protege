package org.protege.editor.owl.model.find;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 16-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLEntityFinder {

    // exact matches

    OWLClass getOWLClass(String rendering);

    OWLObjectProperty getOWLObjectProperty(String rendering);

    OWLDataProperty getOWLDataProperty(String rendering);

    OWLAnnotationProperty getOWLAnnotationProperty(String rendering);

    OWLNamedIndividual getOWLIndividual(String rendering);

    OWLDatatype getOWLDatatype(String rendering);

    OWLEntity getOWLEntity(String rendering);

    Set<OWLEntity> getOWLEntities(String rendering);

    /**
     * Searches for an entity of the specified type with the specified rendering.
     * @param entityType The type of entity to search for.
     * @param rendering The rendering of the entity to search for.
     * @param <E>
     * @return The entity that has the specified rendering and the specified type.  An empty value will be returned
     * if no such entity was found.
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    default <E extends OWLEntity> Optional<E> getOWLEntity(@Nonnull EntityType<E> entityType, @Nonnull String rendering) {
        checkNotNull(entityType);
        checkNotNull(rendering);
        if(entityType == EntityType.CLASS) {
            return Optional.ofNullable((E) getOWLClass(rendering));
        }
        else if(entityType == EntityType.OBJECT_PROPERTY) {
            return Optional.ofNullable((E) getOWLObjectProperty(rendering));
        }
        else if(entityType == EntityType.DATA_PROPERTY) {
            return Optional.ofNullable((E) getOWLDataProperty(rendering));
        }
        else if(entityType == EntityType.ANNOTATION_PROPERTY) {
            return Optional.ofNullable((E) getOWLAnnotationProperty(rendering));
        }
        else if(entityType == EntityType.NAMED_INDIVIDUAL) {
            return Optional.ofNullable((E) getOWLIndividual(rendering));
        }
        else if(entityType == EntityType.DATATYPE) {
            return Optional.ofNullable((E) getOWLDatatype(rendering));
        }
        else {
            throw new RuntimeException("Unknown EntityType: " + entityType);
        }
    }

    Set<String> getOWLEntityRenderings();


    // pattern matches

    Set<OWLClass> getMatchingOWLClasses(String match);

    Set<OWLClass> getMatchingOWLClasses(String match, boolean fullRegExp);
    
    Set<OWLClass> getMatchingOWLClasses(String match, boolean fullRegExp, int flags);


    Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match);

    Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match, boolean fullRegExp);
    
    Set<OWLObjectProperty> getMatchingOWLObjectProperties(String match, boolean fullRegExp, int flags);


    Set<OWLDataProperty> getMatchingOWLDataProperties(String match);

    Set<OWLDataProperty> getMatchingOWLDataProperties(String match, boolean fullRegExp);
    
    Set<OWLDataProperty> getMatchingOWLDataProperties(String match, boolean fullRegExp, int flags);


    Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match);

    Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match, boolean fullRegExp);
    
    Set<OWLNamedIndividual> getMatchingOWLIndividuals(String match, boolean fullRegExp, int flags);


    Set<OWLDatatype> getMatchingOWLDatatypes(String match);

    Set<OWLDatatype> getMatchingOWLDatatypes(String match, boolean fullRegExp);
    
    Set<OWLDatatype> getMatchingOWLDatatypes(String match, boolean fullRegExp, int flags);


    Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match);

    Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match, boolean fullRegExp);
    
    Set<OWLAnnotationProperty> getMatchingOWLAnnotationProperties(String match, boolean fullRegExp, int flags);


    Set<OWLEntity> getMatchingOWLEntities(String match);

    Set<OWLEntity> getMatchingOWLEntities(String match, boolean fullRegExp);
    
    Set<OWLEntity> getMatchingOWLEntities(String match, boolean fullRegExp, int flags);


    // IRI

    Set<OWLEntity> getEntities(IRI iri);
}
