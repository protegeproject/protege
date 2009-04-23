package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.protege.editor.owl.ui.frame.editor.OWLObjectPropertyEditor;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSubObjectPropertyOfAxiom;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLSubObjectPropertyOfAxiom, OWLObjectProperty> {

    private OWLFrameSection section;

    public OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                                 OWLOntology ontology, OWLObjectProperty rootObject,
                                                                 OWLSubObjectPropertyOfAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.section = section;
    }


    protected OWLFrameSectionRowObjectEditor<OWLObjectProperty> getObjectEditor() {
        OWLObjectPropertyEditor editor = (OWLObjectPropertyEditor) section.getEditor();
        OWLObjectPropertyExpression p = getAxiom().getSuperProperty();
        if (!p.isAnonymous()){
            editor.setEditedObject(p.asOWLObjectProperty());
        }
        return editor;
    }


    protected OWLSubObjectPropertyOfAxiom createAxiom(OWLObjectProperty editedObject) {
        return getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(getRoot(), editedObject);
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
