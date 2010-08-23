package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredClassAssertionAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubDataPropertyAxiomGenerator;
import org.semanticweb.owlapi.util.InferredSubObjectPropertyAxiomGenerator;
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
 * Date: 14-Oct-2007<br><br>
 */
public class InferredAxiomsFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLAxiom, OWLAxiom>{

    public InferredAxiomsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> frame) {
        super(editorKit, "Inferred axioms", "Inferred axiom", frame);
    }

    
    protected void clear() {

    }


    protected OWLAxiom createAxiom(OWLAxiom object) {
        return object;
    }


    public OWLObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }


    protected void refill(OWLOntology ontology) {
    }


    protected void refillInferred() {
       try {
           for (OWLClass cls : getReasoner().getUnsatisfiableClasses()) {
               if (!cls.isOWLNothing()) {
                   OWLAxiom unsatAx = getOWLDataFactory().getOWLSubClassOfAxiom(cls,
                                                                              getOWLDataFactory().getOWLNothing());
                   addRow(new InferredAxiomsFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), unsatAx));
               }
           }
           OWLOntologyManager man = OWLManager.createOWLOntologyManager();
           OWLOntology inferredOnt = man.createOntology(IRI.create("http://another.com/ontology" + System.currentTimeMillis()));
           InferredOntologyGenerator ontGen = new InferredOntologyGenerator(getOWLModelManager().getReasoner(), new ArrayList<InferredAxiomGenerator<? extends OWLAxiom>>());
           ontGen.addGenerator(new InferredSubClassAxiomGenerator());
           ontGen.addGenerator(new InferredClassAssertionAxiomGenerator());
           ontGen.addGenerator(new InferredSubObjectPropertyAxiomGenerator());
           ontGen.addGenerator(new InferredSubDataPropertyAxiomGenerator());
           ontGen.fillOntology(man, inferredOnt);


           for (OWLAxiom ax : new TreeSet<OWLAxiom>(inferredOnt.getAxioms())) {
               boolean add = true;
               for (OWLOntology actOnt : getOWLModelManager().getActiveOntologies()) {
                   if (actOnt.containsAxiom(ax)) {
                       add = false;
                       break;
                   }
               }
               if (add) {
                   if (ax instanceof OWLSubClassOfAxiom) {
                       OWLSubClassOfAxiom subClsAx = (OWLSubClassOfAxiom) ax;
                       if (!subClsAx.getSuperClass().isOWLThing()) {
                           addRow(new InferredAxiomsFrameSectionRow(getOWLEditorKit(),
                                                                    this,
                                                                    null,
                                                                    getRootObject(),
                                                                    ax));
                       }
                   }
                   else {
                       addRow(new InferredAxiomsFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), ax));
                   }
               }
           }
       }
       catch (Exception e) {
           e.printStackTrace();
       }
   }


    public Comparator<OWLFrameSectionRow<OWLOntology, OWLAxiom, OWLAxiom>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLOntology, OWLAxiom, OWLAxiom>>() {

            public int compare(OWLFrameSectionRow<OWLOntology, OWLAxiom, OWLAxiom> o1,
                               OWLFrameSectionRow<OWLOntology, OWLAxiom, OWLAxiom> o2) {

                int diff = o1.getAxiom().compareTo(o2.getAxiom());
                if(diff != 0) {
                    return diff;
                }
                else if (o1.getOntology() == null  && o2.getOntology() == null) {
                	return 0;
                }
                else if (o1.getOntology() == null) {
                	return -1;
                }
                else if (o2.getOntology() == null) {
                	return +1;
                }
                else {
                	return o1.getOntology().compareTo(o2.getOntology());
                }
            }
        };
    }
}
