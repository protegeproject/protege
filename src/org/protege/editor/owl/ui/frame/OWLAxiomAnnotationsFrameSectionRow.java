package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.*;

import java.util.Collections;
import java.util.List;
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
public class OWLAxiomAnnotationsFrameSectionRow extends AbstractOWLFrameSectionRow<OWLAxiom, OWLAnnotationAssertionAxiom, OWLAnnotation>{

    private OWLAnnotation annotation;

    public OWLAxiomAnnotationsFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section, OWLOntology ontology,
                                              OWLAxiom rootObject, OWLAnnotationAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.annotation = axiom.getAnnotation();
    }


    protected OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        OWLAnnotationEditor editor = new OWLAnnotationEditor(getOWLEditorKit());
        editor.setAnnotation(getAxiom().getAnnotation());
        return editor;
    }


    protected OWLAnnotationAssertionAxiom createAxiom(OWLAnnotation editedObject) {
        return null;
//        return getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRoot(), editedObject);
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        return Collections.singletonList(annotation);
    }
}
