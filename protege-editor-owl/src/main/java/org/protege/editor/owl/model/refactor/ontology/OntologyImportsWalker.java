package org.protege.editor.owl.model.refactor.ontology;

import java.util.HashSet;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLOntologyHierarchyProvider;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 20, 2008<br><br>
 */
public class OntologyImportsWalker {

    private OWLOntologyHierarchyProvider hp;

    private Set<OWLOntology> ontologies;


    public OntologyImportsWalker(OWLModelManager mngr, Set<OWLOntology> ontologies) {
        this.hp = new OWLOntologyHierarchyProvider(mngr);
        this.ontologies = ontologies;
        hp.setOntologies(ontologies);

    }


    public Set<OWLOntology> getLowestOntologiesToContainReference(OWLEntity entity) {
        Set<OWLOntology> referencingOntologies = getReferencingOntologies(entity);
        return getLeaves(referencingOntologies);
    }


    private Set<OWLOntology> getReferencingOntologies(OWLEntity entity) {
        Set<OWLOntology> referencingOntologies = new HashSet<>();
        for (OWLOntology ont : ontologies){
            if (ont.containsEntityInSignature(entity)){
                referencingOntologies.add(ont);
            }
        }
        return referencingOntologies;
    }


    /**
     * Where 2 ontologies import each other (they are both ancestors of each other)
     * we do not want to remove both of them - always leave a single ontology.
     *
     * @param onts the set of (connected) ontologies to filter
     * @return the "lowest level" ontologies in the graph
     */
    private Set<OWLOntology> getLeaves(Set<OWLOntology> onts) {
        Set<OWLOntology> leaves = new HashSet<>(onts);
        for (OWLOntology ont : onts){
            if (leaves.size() > 1){
                Set<OWLOntology> ancestors = hp.getAncestors(ont);
                ancestors.remove(ont);
                for (OWLOntology ancestor : ancestors){
                    if (leaves.size() > 1){
                        leaves.remove(ancestor);
                    }
                    else{
                        break;
                    }
                }
            }
            else{
                break;
            }
        }
        return leaves;
    }


    public void dispose() {
        hp.dispose();
    }
}
