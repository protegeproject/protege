package org.protege.editor.owl.model.entity;

import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class HomeOntologySupplier {

    @Nonnull
    public OWLOntology getHomeOntology(OWLEntity entity, Set<OWLOntology> ontologies) {
        return ontologies.stream()
                         .filter(o -> o.containsEntityInSignature(entity))
                         .filter(o -> o.isDeclared(entity))
                         .findFirst().orElse(ontologies.iterator().next());
    }

}
