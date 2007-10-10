package org.protege.editor.owl.model.refactor;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AnnotationDeleter {

    private Set<OWLOntology> ontologies;


    public AnnotationDeleter(Set<OWLOntology> ontologies) {
        this.ontologies = new HashSet<OWLOntology>();
    }
}
