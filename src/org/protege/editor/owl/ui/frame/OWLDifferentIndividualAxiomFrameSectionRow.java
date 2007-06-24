package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLDifferentIndividualAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLIndividual, OWLDifferentIndividualsAxiom, Set<OWLIndividual>> {

    public OWLDifferentIndividualAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                      OWLOntology ontology, OWLIndividual rootObject,
                                                      OWLDifferentIndividualsAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<Set<OWLIndividual>> getObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }


    protected OWLDifferentIndividualsAxiom createAxiom(Set<OWLIndividual> editedObject) {
        editedObject.add(getRootObject());
        return getOWLDataFactory().getOWLDifferentIndividualsAxiom(editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        Set<OWLIndividual> individuals = new HashSet<OWLIndividual>(getAxiom().getIndividuals());
        individuals.remove(getRootObject());
        return new ArrayList(individuals);
    }
}
