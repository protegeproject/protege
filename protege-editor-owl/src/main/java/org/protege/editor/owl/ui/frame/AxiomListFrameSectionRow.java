package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Dec-2007<br><br>
 */
public class AxiomListFrameSectionRow extends AbstractOWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom> {


    public AxiomListFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection<Set<OWLAxiom>, OWLAxiom, OWLAxiom> section, OWLOntology ontology, Set<OWLAxiom> rootObject,
                                    OWLAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLAxiom> getObjectEditor() {
        return null;
    }


    protected OWLAxiom createAxiom(OWLAxiom editedObject) {
        return null;
    }


    public List<OWLAxiom> getManipulatableObjects() {
        return Arrays.asList(getAxiom());
    }


    public boolean isEditable() {
        return false;
    }


    public boolean isDeleteable() {
        return true;
    }
}
