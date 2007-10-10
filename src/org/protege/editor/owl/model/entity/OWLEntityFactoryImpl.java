package org.protege.editor.owl.model.entity;

import java.net.URI;
import java.util.Arrays;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * The default implementation of OWLEntityFactory.  This simply
 * creates entities that have URIs that have a base corresponding
 * to the active ontology.
 */
public class OWLEntityFactoryImpl implements OWLEntityFactory {

    private OWLModelManager owlModelManager;


    public OWLEntityFactoryImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    private OWLDataFactory getOWLDataFactory() {
        return owlModelManager.getOWLDataFactory();
    }


    public OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, URI baseURI) {
        return getCreationSet(getOWLDataFactory().getOWLClass(createURI(shortName, baseURI)));
    }


    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, URI baseURI) {
        return getCreationSet(getOWLDataFactory().getOWLObjectProperty(createURI(shortName, baseURI)));
    }


    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, URI baseURI) {
        return getCreationSet(getOWLDataFactory().getOWLDataProperty(createURI(shortName, baseURI)));
    }


    public OWLEntityCreationSet<OWLIndividual> createOWLIndividual(String shortName, URI baseURI) {
        return getCreationSet(getOWLDataFactory().getOWLIndividual(createURI(shortName, baseURI)));
    }


    private <E extends OWLEntity> OWLEntityCreationSet<E> getCreationSet(E entity) {
        return new OWLEntityCreationSet<E>(entity,
                                           Arrays.asList(new AddAxiom(owlModelManager.getActiveOntology(),
                                                                      getOWLDataFactory().getOWLDeclarationAxiom(entity))));
    }


    protected URI createURI(String shortName, URI baseURI) {
        // Ensure that the short name will make a valid URI
        shortName = shortName.replace(" ", "_");
        String ontologyURIString;

        if (baseURI == null) {
            // Use the active ontology URI as a base for the URI we
            // want to create.
            ontologyURIString = owlModelManager.getActiveOntology().getURI().toString();
        }
        else {
            ontologyURIString = baseURI.toString();
        }
        if (!ontologyURIString.endsWith("#") && !ontologyURIString.endsWith("/")) {
            return URI.create(ontologyURIString + "#" + shortName);
        }
        else {
            return URI.create(ontologyURIString + shortName);
        }
    }
}
