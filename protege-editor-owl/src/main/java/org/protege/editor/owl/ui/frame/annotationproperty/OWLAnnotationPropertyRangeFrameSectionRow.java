package org.protege.editor.owl.ui.frame.annotationproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLAnnotationPropertyRangeEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Arrays;
import java.util.List;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 4, 2009<br><br>
 */
public class OWLAnnotationPropertyRangeFrameSectionRow extends AbstractOWLFrameSectionRow<OWLAnnotationProperty, OWLAnnotationPropertyRangeAxiom, IRI> {

    public OWLAnnotationPropertyRangeFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLAnnotationProperty, OWLAnnotationPropertyRangeAxiom, IRI> section,
                                                OWLOntology ontology, OWLAnnotationProperty property,
                                                OWLAnnotationPropertyRangeAxiom axiom) {
        super(owlEditorKit, section, ontology, property, axiom);
    }


    protected OWLAnnotationPropertyRangeEditor getObjectEditor() {
        final OWLAnnotationPropertyRangeEditor editor = new OWLAnnotationPropertyRangeEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getRange());
        return editor;
    }


    protected OWLAnnotationPropertyRangeAxiom createAxiom(IRI iri) {
        return getOWLDataFactory().getOWLAnnotationPropertyRangeAxiom(getRootObject(), iri);
    }


    public List<IRI> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getRange());
    }
}