package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.SWRLRuleEditor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.SWRLRule;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 06-Jul-2007<br><br>
 */
public class SWRLRuleFrameSectionRow extends AbstractOWLFrameSectionRow<OWLOntology, SWRLRule, SWRLRule> {

    public SWRLRuleFrameSectionRow(OWLEditorKit owlEditorKit, 
    		                       OWLFrameSection<OWLOntology, SWRLRule, SWRLRule> section, 
    		                       OWLOntology ontology,
                                   OWLOntology rootObject, SWRLRule axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<SWRLRule> getObjectEditor() {
        SWRLRuleEditor editor = new SWRLRuleEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom());
        return editor;
    }


    public boolean isDeleteable() {
        return true;
    }


    public boolean isEditable() {
        return true;
    }


    protected SWRLRule createAxiom(SWRLRule editedObject) {
        return editedObject;
    }


    public List<SWRLRule> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }
}
