package org.protege.editor.owl.ui.frame.annotationproperty;

import java.util.Arrays;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLAnnotationPropertyEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubAnnotationPropertyOfAxiom;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLSubAnnotationPropertyFrameSectionRow extends AbstractOWLFrameSectionRow<OWLAnnotationProperty, OWLSubAnnotationPropertyOfAxiom, OWLAnnotationProperty> {

    public OWLSubAnnotationPropertyFrameSectionRow(OWLEditorKit owlEditorKit, 
    											   OWLFrameSection<OWLAnnotationProperty, OWLSubAnnotationPropertyOfAxiom, OWLAnnotationProperty> section,
    											   OWLOntology ontology, OWLAnnotationProperty property,
    											   OWLSubAnnotationPropertyOfAxiom axiom) {
        super(owlEditorKit, section, ontology, property, axiom);
    }


    protected OWLAnnotationPropertyEditor getObjectEditor() {
        final OWLAnnotationPropertyEditor editor = new OWLAnnotationPropertyEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getSuperProperty());
        return editor;
    }


    protected OWLSubAnnotationPropertyOfAxiom createAxiom(OWLAnnotationProperty property) {
        return getOWLDataFactory().getOWLSubAnnotationPropertyOfAxiom(getRootObject(), property);
    }


    public List<OWLAnnotationProperty> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getSuperProperty());
    }
}
