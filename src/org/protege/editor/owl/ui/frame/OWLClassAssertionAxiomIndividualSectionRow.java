package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jan-2007<br><br>
 */
public class OWLClassAssertionAxiomIndividualSectionRow extends AbstractOWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual> {

    public OWLClassAssertionAxiomIndividualSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<OWLClass, OWLClassAssertionAxiom, OWLIndividual> section,
                                                      OWLOntology ontology, OWLClass rootObject,
                                                      OWLClassAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLIndividual> getObjectEditor() {
        return null;
    }


    protected OWLClassAssertionAxiom createAxiom(OWLIndividual editedObject) {
        return getOWLDataFactory().getOWLClassAssertionAxiom(editedObject, getRoot());
    }


    public boolean isFixedHeight() {
        return true;
    }
    

    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        return Arrays.asList(getAxiom().getIndividual());
    }
}
