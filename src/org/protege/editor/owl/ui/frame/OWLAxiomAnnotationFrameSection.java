package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

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
 * Date: 02-Jul-2007<br><br>
 */
public class OWLAxiomAnnotationFrameSection extends AbstractOWLFrameSection<OWLAxiom, OWLAnnotationAxiom, OWLAnnotation> {


    public OWLAxiomAnnotationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLAxiom> owlFrame) {
        super(editorKit, "Axiom Annotations", "Axiom Annotation", owlFrame);
    }


    protected OWLAnnotationAxiom createAxiom(OWLAnnotation object) {
        OWLAxiom rootObject = getRootObject();
        return getOWLDataFactory().getOWLAxiomAnnotationAxiom(rootObject, object);
    }


    public OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        return new OWLAnnotationEditor(getOWLEditorKit());
    }


    protected void refill(OWLOntology ontology) {
        for (OWLAnnotationAxiom ax : ontology.getAnnotations(getRootObject())) {
            addRow(new OWLAxiomAnnotationFrameSectionRow(getOWLEditorKit(),
                                                         this,
                                                         ontology,
                                                         ax,
                                                         ax,
                                                         ax.getAnnotation()));
        }
    }


    protected void clear() {
    }


    public Comparator<OWLFrameSectionRow<OWLAxiom, OWLAnnotationAxiom, OWLAnnotation>> getRowComparator() {
        return null;
    }


    public void visit(OWLAxiomAnnotationAxiom axiom) {
        if (getRootObject() == null) {
            return;
        }
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
