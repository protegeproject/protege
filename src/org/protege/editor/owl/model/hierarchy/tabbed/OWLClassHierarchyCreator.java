package org.protege.editor.owl.model.hierarchy.tabbed;

import org.semanticweb.owl.model.*;

import java.util.*;
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
 * Date: 16-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassHierarchyCreator extends AbstractOWLObjectHierarchyCreator {

    private OWLClass root;

    private boolean makeDisjoint;

    private Map<OWLClass, Set<OWLClass>> parent2ChildMap;


    public OWLClassHierarchyCreator(OWLDataFactory dataFactory, OWLClass root, boolean makeDisjoint,
                                    OWLOntology ontology, List<Edge> edges) {
        super(dataFactory, ontology, edges);
        this.root = root;
        this.makeDisjoint = makeDisjoint;
        parent2ChildMap = new HashMap<OWLClass, Set<OWLClass>>();
    }


    private void addToMap(OWLClass parent, OWLClass child) {
        Set<OWLClass> children = parent2ChildMap.get(parent);
        if (children == null) {
            children = new HashSet<OWLClass>();
            parent2ChildMap.put(parent, children);
        }
        children.add(child);
    }


    protected List<OWLOntologyChange> hierarchyCreationStart() {
        parent2ChildMap.clear();
        return super.hierarchyCreationStart();
    }


    protected List<OWLOntologyChange> hierarchyCreationEnd() {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        if (makeDisjoint) {
            for (Set<OWLClass> clses : parent2ChildMap.values()) {
                if (clses.size() > 1) {
                    changes.add(new AddAxiom(getOntology(), getDataFactory().getOWLDisjointClassesAxiom(clses)));
                }
            }
        }
        return changes;
    }


    public OWLOntologyChange getChange(String objName, OWLDataFactory dataFactory) {
        OWLClass cls = dataFactory.getOWLClass(getURI(objName));
        addToMap(root, cls);
        return new AddAxiom(getOntology(), dataFactory.getOWLSubClassAxiom(cls, root));
    }


    public OWLOntologyChange getChange(String childName, String parentName, OWLDataFactory dataFactory) {
        OWLClass sub = dataFactory.getOWLClass(getURI(childName));
        OWLClass sup = dataFactory.getOWLClass(getURI(parentName));
        addToMap(sup, sub);
        return new AddAxiom(getOntology(), dataFactory.getOWLSubClassAxiom(sub, sup));
    }
}
