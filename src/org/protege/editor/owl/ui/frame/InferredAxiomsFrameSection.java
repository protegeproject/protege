package org.protege.editor.owl.ui.frame;

import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.*;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.apibinding.OWLManager;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.ui.OWLEntityComparator;

import java.util.*;
import java.net.URI;
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
        super(editorKit, "Inferred axioms", frame);
    }

    
    protected void clear() {

    }


    protected OWLAxiom createAxiom(OWLAxiom object) {
        return object;
    }


    public OWLFrameSectionRowObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }


    protected void refill(OWLOntology ontology) {
    }


    protected void refillInferred() throws OWLReasonerException {
        try {
            for(OWLClass cls : getReasoner().getInconsistentClasses()) {
                if (!cls.isOWLNothing()) {
                    OWLAxiom unsatAx = getOWLDataFactory().getOWLSubClassAxiom(cls, getOWLDataFactory().getOWLNothing());
                    addRow(new InferredAxiomsFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), unsatAx));
                }
            }
            OWLOntology inferredOnt = getOWLOntologyManager().createOntology(URI.create("http://another.com/ontology" + System.currentTimeMillis()));
            InferredOntologyGenerator ontGen = new InferredOntologyGenerator(getOWLModelManager().getReasoner(), new ArrayList());
            ontGen.addGenerator(new InferredSubClassAxiomGenerator());
            ontGen.addGenerator(new InferredClassAssertionAxiomGenerator());
            ontGen.fillOntology(getOWLOntologyManager(), inferredOnt);

            Set<OWLClass> entities = new TreeSet<OWLClass>(new OWLEntityComparator<OWLClass>(getOWLModelManager()));
            for(OWLOntology o : getOWLModelManager().getActiveOntologies()) {
                entities.addAll(o.getReferencedClasses());
            }
            for(OWLClass cls : entities) {
                for(OWLAxiom ax : inferredOnt.getAxioms(cls)) {
                    boolean add = true;
                    for (OWLOntology actOnt : getOWLModelManager().getActiveOntologies()) {
                        if(actOnt.containsAxiom(ax)) {
                            add = false;
                            break;
                        }
                    }
                    if(add) {
                        addRow(new InferredAxiomsFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), ax));
                    }
                }
            }


            Set<OWLIndividual> inds = new TreeSet<OWLIndividual>(new OWLEntityComparator<OWLIndividual>(getOWLModelManager()));
            for(OWLOntology o : getOWLModelManager().getActiveOntologies()) {
                inds.addAll(o.getReferencedIndividuals());
            }
            for(OWLIndividual ind : inds) {
                for(OWLAxiom ax : inferredOnt.getAxioms(ind)) {
                    boolean add = true;
                    for (OWLOntology actOnt : getOWLModelManager().getActiveOntologies()) {
                        if(actOnt.containsAxiom(ax)) {
                            add = false;
                            break;
                        }
                    }
                    if(add) {
                        addRow(new InferredAxiomsFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), ax));
                    }
                }
            }

            getOWLOntologyManager().removeOntology(inferredOnt.getURI());


        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Comparator<OWLFrameSectionRow<OWLOntology, OWLAxiom, OWLAxiom>> getRowComparator() {
        return null;
    }
}
