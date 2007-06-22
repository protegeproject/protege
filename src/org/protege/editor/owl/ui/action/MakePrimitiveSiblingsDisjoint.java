package org.protege.editor.owl.ui.action;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;

import java.awt.event.ActionEvent;
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
 * Medical Informatics Group<br>
 * Date: 30-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MakePrimitiveSiblingsDisjoint extends SelectedOWLClassAction {

    private static final Logger logger = Logger.getLogger(MakePrimitiveSiblingsDisjoint.class);


    protected void initialiseAction() throws Exception {

    }


    public void actionPerformed(ActionEvent e) {
        OWLClass selCls = getOWLClass();
        if (selCls == null) {
            return;
        }
        // TODO: Extract this and make less dependent on hierarchy provider
        OWLObjectHierarchyProvider<OWLClass> provider = getOWLModelManager().getOWLClassHierarchyProvider();
        Set<OWLClass> clses = new HashSet<OWLClass>();
        for (OWLClass par : provider.getParents(selCls)) {
            clses.addAll(provider.getChildren(par));
        }
        OWLAxiom ax = getOWLDataFactory().getOWLDisjointClassesAxiom(clses);
        getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));

        // 2) Get the named subs

//        try {
//            OWLOntology owlOntology = getOWLModelManager().getActiveOntology();
//            DisjointAxiomCreator creator = new DisjointAxiomCreator(getOWLModelManager().getOWLClassHierarchyProvider(),
//                                                                    owlOntology,
//                                                                    getOWLModelManager().getActiveOntologies());
//
//            getOWLModelManager().applyChanges(creator.getChanges());
//        } catch (OWLException ex) {
//            logger.error(ex);
//        }
    }
}
