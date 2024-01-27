package org.protege.editor.owl.model.hierarchy;

import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLOntologyObjectHierarchyProvider<N extends OWLObject> extends OWLObjectHierarchyProvider<N> {

    public void setOntologies(Set<OWLOntology> ontologies);


    public void handleOntologyChanges(List<? extends OWLOntologyChange> changes);
}
