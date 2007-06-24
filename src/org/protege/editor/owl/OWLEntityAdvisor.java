package org.protege.editor.owl;

import java.util.Set;

import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 24, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLEntityAdvisor {

    public void setOntologies(Set<OWLOntology> ontologies);


    public Set<OWLOntology> getOntologies();


    public void rebuild();
}
