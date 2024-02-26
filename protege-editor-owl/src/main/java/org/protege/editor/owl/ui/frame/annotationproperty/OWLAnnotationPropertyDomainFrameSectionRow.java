package org.protege.editor.owl.ui.frame.annotationproperty;

import java.util.Arrays;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLAnnotationPropertyDomainEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLAnnotationPropertyDomainFrameSectionRow extends AbstractOWLFrameSectionRow<OWLAnnotationProperty, OWLAnnotationPropertyDomainAxiom, IRI> {

    public OWLAnnotationPropertyDomainFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLAnnotationProperty, OWLAnnotationPropertyDomainAxiom, IRI> section,
                                                OWLOntology ontology, OWLAnnotationProperty property,
                                                OWLAnnotationPropertyDomainAxiom axiom) {
        super(owlEditorKit, section, ontology, property, axiom);
    }


    protected OWLAnnotationPropertyDomainEditor getObjectEditor() {
        final OWLAnnotationPropertyDomainEditor editor = new OWLAnnotationPropertyDomainEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getDomain());
        return editor;
    }


    protected OWLAnnotationPropertyDomainAxiom createAxiom(IRI iri) {
        return getOWLDataFactory().getOWLAnnotationPropertyDomainAxiom(getRootObject(), iri);
    }


    public List<IRI> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getDomain());
    }
}
