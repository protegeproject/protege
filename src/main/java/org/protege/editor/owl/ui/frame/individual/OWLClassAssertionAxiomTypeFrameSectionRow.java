package org.protege.editor.owl.ui.frame.individual;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLClassAssertionAxiomTypeFrameSectionRow extends AbstractOWLFrameSectionRow<OWLIndividual, OWLClassAssertionAxiom, OWLClassExpression> {

    public OWLClassAssertionAxiomTypeFrameSectionRow(OWLEditorKit owlEditorKit, 
    												 OWLFrameSection<OWLIndividual, OWLClassAssertionAxiom, OWLClassExpression> section,
                                                     OWLOntology ontology, OWLIndividual rootObject,
                                                     OWLClassAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(getAxiom().getClassExpression(), AxiomType.CLASS_ASSERTION);
    }


    protected OWLClassAssertionAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLClassAssertionAxiom(editedObject, getRootObject());
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLClassExpression> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getClassExpression());
    }
}
