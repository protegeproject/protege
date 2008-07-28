package org.protege.editor.owl.model.entity;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;

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

    public OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI);


    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, URI baseURI);


    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, URI baseURI);


    public OWLEntityCreationSet<OWLIndividual> createOWLIndividual(String shortName, URI baseURI);


    /**
     * Check that the ID that will be generated will be valid for a new entity
     * @param shortName
     * @param baseURI
     * @return true if the URI that would be generated is valid and unique
     */
    boolean isValidNewID(String shortName, URI baseURI);
}
