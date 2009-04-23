package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLClassExpression;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSubClassOfAxiom;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLSubClassAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLClassExpression, OWLSubClassOfAxiom, OWLClassExpression> {

    public OWLSubClassAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section, OWLOntology ontology,
                                           OWLClassExpression rootObject, OWLSubClassOfAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLClassExpression> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(getAxiom().getSuperClass(), AxiomType.SUBCLASS);        
    }


    protected OWLSubClassOfAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLSubClassOfAxiom(getRootObject(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.
     */
    public List getManipulatableObjects() {
        return Arrays.asList(getAxiom().getSuperClass());
    }
}
