package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLDataPropertyRangeFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLDataPropertyRangeAxiom, OWLDataRange> {

    public OWLDataPropertyRangeFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section, OWLOntology ontology,
                                               OWLDataProperty rootObject, OWLDataPropertyRangeAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLDataPropertyRangeAxiom createAxiom(OWLDataRange editedObject) {
        return getOWLDataFactory().getOWLDataPropertyRangeAxiom(getRootObject(), editedObject);
    }


    protected OWLFrameSectionRowObjectEditor<OWLDataRange> getObjectEditor() {
        return null;
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getRange());
    }
}
