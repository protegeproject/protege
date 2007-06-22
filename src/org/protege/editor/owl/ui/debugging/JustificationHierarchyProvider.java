package org.protege.editor.owl.ui.debugging;

import org.protege.editor.owl.model.hierarchy.AbstractOWLObjectHierarchyProvider;
import org.semanticweb.owl.debugging.JustificationMap;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;

import java.util.Collections;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Mar-2007<br><br>
 */
public class JustificationHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLAxiom> {

    private OWLDescription description;

    private Set<OWLAxiom> rootAxioms;

    private JustificationMap map;


    public JustificationHierarchyProvider(OWLOntologyManager owlOntologyManager, OWLDescription description,
                                          Set<OWLAxiom> axioms) {
        super(owlOntologyManager);
        this.description = description;
        rootAxioms = new HashSet<OWLAxiom>();
        map = new JustificationMap(description, axioms);
        rootAxioms.addAll(map.getRootAxioms());
    }


    public boolean containsReference(OWLAxiom object) {
        return false;
    }


    public Set<OWLAxiom> getChildren(OWLAxiom object) {
        return map.getChildAxioms(object);
    }


    public Set<OWLAxiom> getEquivalents(OWLAxiom object) {
        return Collections.emptySet();
    }


    public Set<OWLAxiom> getParents(OWLAxiom object) {
        return Collections.emptySet();
    }


    public Set<OWLAxiom> getRoots() {
        return rootAxioms;
    }


    public void setOntologies(Set<OWLOntology> ontologies) {
    }
}
