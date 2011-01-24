package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLGeneralAxiomEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntology;


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


    protected OWLObjectEditor<OWLClassAxiom> getObjectEditor() {
        OWLGeneralAxiomEditor editor =  new OWLGeneralAxiomEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom());
        return editor;
    }

    @Override
    public void handleEditingFinished(Set<OWLClassAxiom> editedObjects) {
    	super.handleEditingFinished(editedObjects);
    	OWLGeneralClassAxiomsFrameSection.checkEditedAxiom(getOWLEditorKit(), editedObjects);
    }
    


    protected OWLClassAxiom createAxiom(OWLClassAxiom editedObject) {
        return editedObject;
    }


    public List<OWLClassAxiom> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }
}
