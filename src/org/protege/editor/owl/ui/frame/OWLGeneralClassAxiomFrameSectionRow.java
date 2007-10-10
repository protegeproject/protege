package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Apr-2007<br><br>
 */
public class OWLGeneralClassAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLOntology, OWLClassAxiom, OWLClassAxiom> {

    public OWLGeneralClassAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section, OWLOntology ontology,
                                               OWLOntology rootObject, OWLClassAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLClassAxiom> getObjectEditor() {
        return null;
    }


    protected OWLClassAxiom createAxiom(OWLClassAxiom editedObject) {
        return null;
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }
}
