package org.protege.editor.owl.model;

import com.google.common.collect.Sets;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.ConcurrentOWLOntologyBuilder;
import uk.ac.manchester.cs.owl.owlapi.concurrent.NonConcurrentOWLOntologyBuilder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
