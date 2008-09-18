package org.protege.editor.owl.model.entity;

import org.semanticweb.owl.model.*;

import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLEntityFactory {

    public OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI) throws OWLEntityCreationException;


    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, URI baseURI) throws OWLEntityCreationException;


    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, URI baseURI) throws OWLEntityCreationException;


    public OWLEntityCreationSet<OWLIndividual> createOWLIndividual(String shortName, URI baseURI) throws OWLEntityCreationException;


    public <T extends OWLEntity> OWLEntityCreationSet<T> createOWLEntity(Class<T> type, String shortName, URI baseURI) throws OWLEntityCreationException;


    /**
     * Use this to check if the entity can be created without affecting any generated IDs
     * @param type
     * @param shortName
     * @param baseURI
     * @throws OWLEntityCreationException if the entity could not be created because of bad input/name clashes/auto ID etc
     */
    public void tryCreate(Class<? extends OWLEntity> type, String shortName, URI baseURI) throws OWLEntityCreationException;

}
