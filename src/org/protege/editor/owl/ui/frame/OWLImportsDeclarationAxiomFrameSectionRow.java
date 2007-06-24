package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 24-Jan-2007<br><br>
 */
public class OWLImportsDeclarationAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLOntology, OWLImportsDeclaration, OWLImportsDeclaration> {

    public OWLImportsDeclarationAxiomFrameSectionRow(OWLEditorKit editorKit, OWLFrameSection section,
                                                     OWLOntology ontology, OWLOntology rootObject,
                                                     OWLImportsDeclaration axiom) {
        super(editorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLImportsDeclaration> getObjectEditor() {
        return null;
    }


    protected OWLImportsDeclaration createAxiom(OWLImportsDeclaration editedObject) {
        return editedObject;
    }


    public boolean isDeletable() {
        return super.isEditable();
    }


    public boolean isEditable() {
        return false;
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }
}
