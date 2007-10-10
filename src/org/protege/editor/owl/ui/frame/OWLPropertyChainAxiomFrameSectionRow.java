package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLPropertyChainAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLObjectPropertyChainSubPropertyAxiom, List<OWLObjectPropertyExpression>> {

    public OWLPropertyChainAxiomFrameSectionRow(OWLEditorKit editorKit, OWLFrameSection section, OWLOntology ontology,
                                                OWLObjectProperty rootObject,
                                                OWLObjectPropertyChainSubPropertyAxiom axiom) {
        super(editorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<List<OWLObjectPropertyExpression>> getObjectEditor() {
        OWLObjectPropertyChainEditor editor = new OWLObjectPropertyChainEditor(getOWLEditorKit());
        editor.setAxiom(getAxiom());
        return editor;
    }


    protected OWLObjectPropertyChainSubPropertyAxiom createAxiom(List<OWLObjectPropertyExpression> editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyChainSubPropertyAxiom(editedObject, getRootObject());
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }
}
