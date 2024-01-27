package org.protege.editor.owl.ui.frame.objectproperty;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyChainEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLPropertyChainAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLSubPropertyChainOfAxiom, List<OWLObjectPropertyExpression>> {

    public OWLPropertyChainAxiomFrameSectionRow(OWLEditorKit editorKit, 
    											OWLFrameSection<OWLObjectProperty, OWLSubPropertyChainOfAxiom, List<OWLObjectPropertyExpression>> section, 
    											OWLOntology ontology,
                                                OWLObjectProperty rootObject,
                                                OWLSubPropertyChainOfAxiom axiom) {
        super(editorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<List<OWLObjectPropertyExpression>> getObjectEditor() {
        OWLObjectPropertyChainEditor editor = new OWLObjectPropertyChainEditor(getOWLEditorKit());
        editor.setAxiom(getAxiom());
        return editor;
    }


    protected OWLSubPropertyChainOfAxiom createAxiom(List<OWLObjectPropertyExpression> editedObject) {
        return getOWLDataFactory().getOWLSubPropertyChainOfAxiom(editedObject, getRootObject());
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLSubPropertyChainOfAxiom> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }
}
