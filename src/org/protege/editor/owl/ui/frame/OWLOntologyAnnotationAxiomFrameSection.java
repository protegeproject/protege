package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;

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
 * Date: 04-Feb-2007<br><br>
 */
public class OWLOntologyAnnotationAxiomFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLOntologyAnnotationAxiom, OWLAnnotation> {

    public static final String LABEL = "Annotations";


    public OWLOntologyAnnotationAxiomFrameSection(OWLEditorKit owlEditorKit, OWLFrame<? extends OWLOntology> frame) {
        super(owlEditorKit, LABEL, frame);
    }


    protected OWLOntologyAnnotationAxiom createAxiom(OWLAnnotation object) {
        return getOWLDataFactory().getOWLOntologyAnnotationAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        return new OWLAnnotationEditor(getOWLEditorKit());
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLOntologyAnnotationAxiom ax : getRootObject().getAnnotations(ontology)) {
            if (!getOWLEditorKit().getOWLWorkspace().isHiddenAnnotationURI(ax.getAnnotation().getAnnotationURI())) {
                addRow(new OWLOntologyAnnotationAxiomFrameSectionRow(getOWLEditorKit(),
                                                                     this,
                                                                     ontology,
                                                                     getRootObject(),
                                                                     ax));
            }
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLOntology, OWLOntologyAnnotationAxiom, OWLAnnotation>> getRowComparator() {
        return null;
    }


    public void visit(OWLOntologyAnnotationAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
