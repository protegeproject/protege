package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyRangeFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLDataPropertyRangeAxiom, OWLDataRange> {

    private OWLFrameSection section;

    public OWLDataPropertyRangeFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section, OWLOntology ontology,
                                               OWLDataProperty rootObject, OWLDataPropertyRangeAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.section = section;
    }


    protected OWLDataPropertyRangeAxiom createAxiom(OWLDataRange editedObject) {
        return getOWLDataFactory().getOWLDataPropertyRangeAxiom(getRootObject(), editedObject);
    }


    protected OWLFrameSectionRowObjectEditor<OWLDataRange> getObjectEditor() {
        OWLDataRangeEditor editor = (OWLDataRangeEditor)section.getEditor();
        editor.setEditedObject(getAxiom().getRange());
        return editor;
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getRange());
    }
}
