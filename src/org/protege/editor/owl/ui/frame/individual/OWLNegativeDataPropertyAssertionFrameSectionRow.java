package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.editor.OWLDataPropertyRelationshipEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Feb-2007<br><br>
 */
public class OWLNegativeDataPropertyAssertionFrameSectionRow extends AbstractOWLFrameSectionRow<OWLIndividual, OWLNegativeDataPropertyAssertionAxiom, OWLDataPropertyConstantPair> {

    public OWLNegativeDataPropertyAssertionFrameSectionRow(OWLEditorKit editorKit, OWLFrameSection section,
                                                           OWLOntology ontology, OWLIndividual rootObject,
                                                           OWLNegativeDataPropertyAssertionAxiom axiom) {
        super(editorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLDataPropertyConstantPair> getObjectEditor() {
        OWLDataPropertyRelationshipEditor editor = new OWLDataPropertyRelationshipEditor(getOWLEditorKit());
        editor.setDataPropertyAxiom(getAxiom());
        return editor;
    }


    protected OWLNegativeDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair editedObject) {
        return getOWLDataFactory().getOWLNegativeDataPropertyAssertionAxiom(getRootObject(),
                                                                            editedObject.getProperty(),
                                                                            editedObject.getConstant());
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        List objects = new ArrayList();
        objects.add(getAxiom().getProperty());
        objects.add(getAxiom().getObject());
        return objects;
    }


    public String getDelimeter() {
        return "  ";
    }
}
