package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.frame.editor.OWLIndividualSetEditor;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNamedIndividual;
import org.semanticweb.owl.model.OWLOntology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLDifferentIndividualAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLNamedIndividual, OWLDifferentIndividualsAxiom, Set<OWLNamedIndividual>> {

    public OWLDifferentIndividualAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                      OWLOntology ontology, OWLNamedIndividual rootObject,
                                                      OWLDifferentIndividualsAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<Set<OWLNamedIndividual>> getObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }


    protected OWLDifferentIndividualsAxiom createAxiom(Set<OWLNamedIndividual> editedObject) {
        editedObject.add(getRootObject());
        return getOWLDataFactory().getOWLDifferentIndividualsAxiom(editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLNamedIndividual> getManipulatableObjects() {
        //@@TODO v3 port - what about anon indivs?
        Set<OWLIndividual> individuals = getAxiom().getIndividuals();
        List<OWLNamedIndividual> results = new ArrayList<OWLNamedIndividual>();
        for (OWLIndividual ind : individuals){
            if (!ind.isAnonymous() && !ind.equals(getRootObject())){
                results.add(ind.asNamedIndividual());
            }
        }
        return results;
    }
}
