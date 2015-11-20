package org.protege.editor.owl.ui.frame.dataproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataRangeEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyRangeFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLDataPropertyRangeAxiom, OWLDataRange> {

    public OWLDataPropertyRangeFrameSectionRow(OWLEditorKit owlEditorKit, 
    		                                   OWLFrameSection<OWLDataProperty, OWLDataPropertyRangeAxiom, OWLDataRange> section, OWLOntology ontology,
                                               OWLDataProperty rootObject, OWLDataPropertyRangeAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLDataPropertyRangeAxiom createAxiom(OWLDataRange editedObject) {
        return getOWLDataFactory().getOWLDataPropertyRangeAxiom(getRootObject(), editedObject);
    }


    protected OWLObjectEditor<OWLDataRange> getObjectEditor() {
        OWLDataRangeEditor editor = new OWLDataRangeEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getRange());
        return editor;
    }


    public List<OWLDataRange> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getRange());
    }
}
