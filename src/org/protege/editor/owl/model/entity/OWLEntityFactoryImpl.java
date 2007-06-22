package org.protege.editor.owl.model.entity;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.Arrays;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
