package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLSameIndividualsAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLIndividual, OWLSameIndividualsAxiom, Set<OWLIndividual>> {

    public OWLSameIndividualsAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                  OWLOntology ontology, OWLIndividual rootObject,
                                                  OWLSameIndividualsAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<Set<OWLIndividual>> getObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }


    protected OWLSameIndividualsAxiom createAxiom(Set<OWLIndividual> editedObject) {
        editedObject.add(getRootObject());
        return getOWLDataFactory().getOWLSameIndividualsAxiom(editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        Set<OWLIndividual> individuals = new HashSet<OWLIndividual>(getAxiom().getIndividuals());
        individuals.remove(getRoot());
        return new ArrayList(individuals);
    }
}
