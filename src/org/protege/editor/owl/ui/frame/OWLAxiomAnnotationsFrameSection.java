package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Comparator;
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
 * Date: 06-Dec-2007<br><br>
 *
 * THis is all wrong - the framesection cannot be used as axiom annotations are not annotation assertions
 */
public class OWLAxiomAnnotationsFrameSection extends AbstractOWLFrameSection<OWLAxiom, OWLAnnotationAssertionAxiom, OWLAnnotation>{

    private static final String LABEL = "Annotations";

    public OWLAxiomAnnotationsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLAxiom> owlFrame) {
        super(editorKit, LABEL, "Axiom annotation", owlFrame);
    }


    protected OWLAnnotationAssertionAxiom createAxiom(OWLAnnotation object) {
        return null;
//        return getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        return new OWLAnnotationEditor(getOWLEditorKit());
    }


    public boolean canAdd() {
        return getRootObject() != null;
    }


    protected void refill(OWLOntology ontology) {

//        // no straightforward way to ask the ontology for annotation axioms on a given axiom
        // this won't work as annotations are part of the axiom - annotation assertions are just on
//        Set<OWLAnnotationAssertionAxiom> annotationAssertionsForOntology = new HashSet<OWLAnnotationAssertionAxiom>();
//        for (OWLAnnotation annot : getRootObject().getAnnotations()){
//            OWLAnnotationAssertionAxiom annotAx = getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), annot);
//            if (ontology.containsAxiom(annotAx)){
//                annotationAssertionsForOntology.add(annotAx);
//            }
//        }
//
//        for(OWLAnnotationAssertionAxiom ax : annotationAssertionsForOntology) {
//                addRow(new OWLAxiomAnnotationsFrameSectionRow(getOWLEditorKit(),
//                                                             this,
//                                                             ontology,
//                                                             getRootObject(),
//                                                             ax));
//            }
    }


    protected void clear() {
    }


    public Comparator<OWLFrameSectionRow<OWLAxiom, OWLAnnotationAssertionAxiom, OWLAnnotation>> getRowComparator() {
        return null;
    }


    public void visit(OWLAnnotationAssertionAxiom axiom) {
        final OWLAxiom root = getRootObject();
        if(root != null && root.equals(axiom.getSubject())) {
            reset();
        }
    }
}
