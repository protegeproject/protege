package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyIndividualPairEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Feb-2007<br><br>
 */
public class OWLNegativeObjectPropertyAssertionFrameSectionRow extends AbstractOWLFrameSectionRow<OWLIndividual, OWLNegativeObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair> {

    public OWLNegativeObjectPropertyAssertionFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                             OWLOntology ontology, OWLIndividual rootObject,
                                                             OWLNegativeObjectPropertyAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLObjectPropertyIndividualPair> getObjectEditor() {
        OWLObjectPropertyIndividualPairEditor editor = new OWLObjectPropertyIndividualPairEditor(getOWLEditorKit());
        editor.setObjectPropertyAxiom(getAxiom());
        return editor;
    }


    protected OWLNegativeObjectPropertyAssertionAxiom createAxiom(OWLObjectPropertyIndividualPair editedObject) {
        return getOWLDataFactory().getOWLNegativeObjectPropertyAssertionAxiom(getRootObject(),
                                                                              editedObject.getProperty(),
                                                                              editedObject.getIndividual());
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
