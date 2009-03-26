package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyRangeFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLObjectPropertyRangeAxiom, OWLDescription> {

    public OWLObjectPropertyRangeFrameSectionRow(OWLEditorKit editorKit, OWLFrameSection section, OWLOntology ontology,
                                                 OWLObjectProperty rootObject, OWLObjectPropertyRangeAxiom axiom) {
        super(editorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLDescription> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(getAxiom().getRange(), AxiomType.OBJECT_PROPERTY_RANGE);
    }


    protected OWLObjectPropertyRangeAxiom createAxiom(OWLDescription editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyRangeAxiom(getRoot(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        return Arrays.asList(getAxiom().getRange());
    }
}
