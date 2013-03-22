package org.protege.editor.owl.ui.frame.individual;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyIndividualPairEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLObjectPropertyAssertionAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLIndividual, OWLObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair> {

    public OWLObjectPropertyAssertionAxiomFrameSectionRow(OWLEditorKit owlEditorKit, 
    													  OWLFrameSection<OWLIndividual, OWLObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair> section,
                                                          OWLOntology ontology, OWLIndividual rootObject,
                                                          OWLObjectPropertyAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLObjectPropertyIndividualPair> getObjectEditor() {
        OWLObjectPropertyIndividualPairEditor editor = new OWLObjectPropertyIndividualPairEditor(getOWLEditorKit());
        editor.setObjectPropertyAxiom(getAxiom());
        return editor;
    }


    protected OWLObjectPropertyAssertionAxiom createAxiom(OWLObjectPropertyIndividualPair editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(editedObject.getProperty(),
                                                                      getRootObject(),
                                                                      editedObject.getIndividual());
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLObject> getManipulatableObjects() {
        List<OWLObject> objects = new ArrayList<OWLObject>(2);
        objects.add(getAxiom().getProperty());
        objects.add(getAxiom().getObject());
        return objects;
    }


    public String getDelimeter() {
        return "  ";
    }
}
