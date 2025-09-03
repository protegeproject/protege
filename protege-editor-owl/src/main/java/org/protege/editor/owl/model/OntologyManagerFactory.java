package org.protege.editor.owl.model;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 4 Sep 2018
 */
public class OntologyManagerFactory {

    @Nonnull
    public static OWLOntologyManager createManager() {
        return OWLManager.createConcurrentOWLOntologyManager();
    }
}
