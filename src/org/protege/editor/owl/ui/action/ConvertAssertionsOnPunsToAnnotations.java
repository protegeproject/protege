package org.protege.editor.owl.ui.action;

import org.semanticweb.owl.model.*;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
 * Date: 23-Jul-2007<br><br>
 */
public class ConvertAssertionsOnPunsToAnnotations extends ProtegeOWLAction {


    public void actionPerformed(ActionEvent e) {

        Set<OWLOntology> onts = getOWLModelManager().getOntologies();
        Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLOntology ont : onts) {
            individuals.addAll(ont.getReferencedIndividuals());
        }

        Set<OWLDataProperty> props = new HashSet<OWLDataProperty>();
        for (OWLIndividual ind : individuals) {
            boolean punned = false;
            for (OWLOntology ont : onts) {
                if (ont.containsClassReference(ind.getURI())) {
                    punned = true;
                    break;
                }
            }
            if (!punned) {
                continue;
            }

            for (OWLOntology ont : onts) {
                for (OWLDataPropertyAssertionAxiom ax : ont.getDataPropertyAssertionAxioms(ind)) {
                    if (!ax.getProperty().isAnonymous()) {
                        changes.add(new RemoveAxiom(ont, ax));
                        OWLDataFactory df = getOWLDataFactory();
                        OWLAnnotation anno = df.getOWLConstantAnnotation(((OWLDataProperty) ax.getProperty()).getURI(),
                                                                         ax.getObject());
                        OWLEntityAnnotationAxiom annoAx = df.getOWLEntityAnnotationAxiom(df.getOWLClass(ind.getURI()),
                                                                                         anno);
                        changes.add(new AddAxiom(ont, annoAx));
                        props.add((OWLDataProperty) ax.getProperty());
                    }
                }
            }
            for (OWLOntology ont : onts) {
                for (OWLAxiom ax : ont.getDeclarationAxioms(ind)) {
                    changes.add(new RemoveAxiom(ont, ax));
                }
                for (OWLClassAssertionAxiom ax : ont.getClassAssertionAxioms(ind)) {
                    changes.add(new RemoveAxiom(ont, ax));
                }
            }
            for (OWLDataProperty prop : props) {
                for (OWLOntology ont : onts) {
                    for (OWLAxiom ax : ont.getDeclarationAxioms(prop)) {
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                    for (OWLAxiom ax : ont.getAxioms(prop)) {
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                }
            }
        }
        getOWLModelManager().applyChanges(changes);
        for (OWLOntology ont : onts) {
            for (OWLDataProperty prop : ont.getReferencedDataProperties()) {
                for (OWLOntology o : onts) {
                    for (OWLAxiom ax : o.getReferencingAxioms(prop)) {
                        System.out.println(ax);
                    }
                }
            }
        }
    }


    public void dispose() throws Exception {

    }


    public void initialise() throws Exception {

    }
}
