package org.protege.editor.owl.ui.frame.ontology;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.protege.editor.owl.ui.frame.InferredAxiomsFrameSection;
import org.semanticweb.owlapi.model.OWLOntology;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Oct-2007<br><br>
 */
public class OWLOntologyFrame extends AbstractOWLFrame<OWLOntology> {


    public OWLOntologyFrame(OWLEditorKit editorKit) {
        super(editorKit.getModelManager().getOWLOntologyManager());
// @@TODO v3 port
//        addSection(new OWLOntologyAnnotationAxiomFrameSection(editorKit, this));
//        addSection(new OWLImportsDeclarationFrameSection(editorKit, this));
//        addSection(new OWLIndirectImportsFrameSection(editorKit, this));
        addSection(new InferredAxiomsFrameSection(editorKit, this));
    }
}
