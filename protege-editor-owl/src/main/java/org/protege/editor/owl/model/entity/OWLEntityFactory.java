package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLEntityFactory {

    /**
     *
     * @param shortName user supplied name
     * @param baseIRI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, IRI baseIRI) throws OWLEntityCreationException;

    /**
     *
     * @param shortName user supplied name
     * @param baseIRI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, IRI baseIRI) throws OWLEntityCreationException;

    /**
     *
     * @param shortName user supplied name
     * @param baseIRI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, IRI baseIRI) throws OWLEntityCreationException;


    /**
     *
     * @param shortName user supplied name
     * @param baseIRI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLAnnotationProperty> createOWLAnnotationProperty(String shortName, IRI baseIRI) throws OWLEntityCreationException;


    /**
     *
     * @param shortName user supplied name
     * @param baseIRI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLNamedIndividual> createOWLIndividual(String shortName, IRI baseIRI) throws OWLEntityCreationException;


    /**
     *
     * @param shortName user supplied name
     * @param baseIRI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    OWLEntityCreationSet<OWLDatatype> createOWLDatatype(String shortName, IRI baseIRI) throws OWLEntityCreationException;


    /**
     *
     * @param type OWLClass, OWLObjectProperty, OWLDataProperty, OWLIndividual, OWLDatatype
     * @param shortName user supplied name
     * @param baseIRI specify a base or leave as null to let the factory decide
     * @return an object wrapping the changes that need to be applied
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    <T extends OWLEntity> OWLEntityCreationSet<T> createOWLEntity(Class<T> type, String shortName, IRI baseIRI) throws OWLEntityCreationException;


    /**
     * Use this to check if the entity can be created without affecting any generated IDs
     * @param type OWLClass, OWLObjectProperty, OWLDataProperty or OWLIndividual
     * @param shortName user supplied name
     * @param baseIRI specify a base or leave as null to let the factory decide
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     * @return an entity creation set - this should never be applied to the ontology
     */
    <T extends OWLEntity> OWLEntityCreationSet<T> preview(Class<T> type, String shortName, IRI baseIRI) throws OWLEntityCreationException;

}
