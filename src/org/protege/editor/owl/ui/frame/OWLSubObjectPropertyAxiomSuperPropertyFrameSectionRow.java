package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLObjectSubPropertyAxiom, OWLObjectProperty> {

    public OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                                 OWLOntology ontology, OWLObjectProperty rootObject,
                                                                 OWLObjectSubPropertyAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLObjectProperty> getObjectEditor() {
        return new OWLObjectPropertyEditor(getOWLEditorKit());
    }


    protected OWLObjectSubPropertyAxiom createAxiom(OWLObjectProperty editedObject) {
        return getOWLDataFactory().getOWLSubObjectPropertyAxiom(getRoot(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        return Arrays.asList(getAxiom().getSuperProperty());
    }
}
