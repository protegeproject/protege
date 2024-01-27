package org.protege.editor.owl.ui.frame.individual;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertyRelationshipEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLDataPropertyAssertionAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLIndividual, OWLDataPropertyAssertionAxiom, OWLDataPropertyConstantPair> {

    public OWLDataPropertyAssertionAxiomFrameSectionRow(OWLEditorKit editorKit, 
    		                                            OWLFrameSection<OWLIndividual, OWLDataPropertyAssertionAxiom, OWLDataPropertyConstantPair> section,
                                                        OWLOntology ontology, OWLIndividual rootObject,
                                                        OWLDataPropertyAssertionAxiom axiom) {
        super(editorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLDataPropertyConstantPair> getObjectEditor() {
        OWLDataPropertyRelationshipEditor editor = new OWLDataPropertyRelationshipEditor(getOWLEditorKit());
        editor.setDataPropertyAxiom(getAxiom());
        return editor;
    }


    protected OWLDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair editedObject) {
        return getOWLDataFactory().getOWLDataPropertyAssertionAxiom(editedObject.getProperty(),
                                                                    getRootObject(),
                                                                    editedObject.getConstant());
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLObject> getManipulatableObjects() {
        List<OWLObject> objects = new ArrayList<>();
        objects.add(getAxiom().getProperty());
        objects.add(getAxiom().getObject());
        return objects;
    }


    /**
     * Gets the object which is presented to the use.  This object
     * usually forms part of the axiom.  For example, the right hand
     * side of a subclass axiom.
     */
    public Object getRowObject() {
        return getAxiom();
    }


    /**
     * Gets the class of the object - this determines the editor
     * to use to edit the object
     */
    public Class getRowObjectClass() {
        return OWLDataPropertyAssertionAxiom.class;
    }


    public String getDelimeter() {
        return "  ";
    }
}
