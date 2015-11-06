package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.*;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jan-2007<br><br>
 */
public class OWLClassAssertionAxiomMembersSectionRow extends AbstractOWLFrameSectionRow<OWLClassExpression, OWLClassAssertionAxiom, OWLNamedIndividual> {

    public OWLClassAssertionAxiomMembersSectionRow(OWLEditorKit owlEditorKit, 
    											   OWLFrameSection<OWLClassExpression, OWLClassAssertionAxiom, OWLNamedIndividual> section,
    											   OWLOntology ontology, OWLClassExpression rootObject,
    											   OWLClassAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLNamedIndividual> getObjectEditor() {
        return null;
    }


    public boolean isEditable() {
        return false;
    }

    @Override
    public boolean isDeleteable() {
        return true;
    }
    
    protected OWLClassAssertionAxiom createAxiom(OWLNamedIndividual editedObject) {
        return getOWLDataFactory().getOWLClassAssertionAxiom(getRoot(), editedObject);
    }


    public boolean isFixedHeight() {
        return true;
    }
    

    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLIndividual> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getIndividual());
    }
}
