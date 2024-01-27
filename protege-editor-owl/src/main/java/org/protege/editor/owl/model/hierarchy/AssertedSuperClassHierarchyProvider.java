package org.protege.editor.owl.model.hierarchy;

import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AssertedSuperClassHierarchyProvider extends AbstractSuperClassHierarchyProvider {

    private OWLModelManager owlModelManager;


    public AssertedSuperClassHierarchyProvider(OWLModelManager owlModelManager) {
        super(owlModelManager.getOWLOntologyManager());
        this.owlModelManager = owlModelManager;
    }


    public Set<OWLClass> getUnfilteredChildren(OWLClass object) {
        return owlModelManager.getOWLHierarchyManager().getOWLClassHierarchyProvider().getParents(object);
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
        return Collections.emptySet();
    }


    public Set<OWLClass> getParents(OWLClass object) {
        return owlModelManager.getOWLHierarchyManager().getOWLClassHierarchyProvider().getChildren(object);
    }


    public boolean containsReference(OWLClass object) {
        return false;
    }


    public void rebuild() {
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies) {
    }
}
