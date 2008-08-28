package org.protege.editor.owl.model.refactor.ontology;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLOntologyHierarchyProvider;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;

import java.util.HashSet;
import java.util.Set;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
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
        Set<OWLOntology> referencingOntologies = new HashSet<OWLOntology>();
        for (OWLOntology ont : ontologies){
            if (ont.containsEntityReference(entity)){
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
        Set<OWLOntology> leaves = new HashSet<OWLOntology>(onts);
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
