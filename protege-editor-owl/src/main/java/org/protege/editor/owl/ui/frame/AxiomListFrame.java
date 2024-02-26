package org.protege.editor.owl.ui.frame;

import java.util.Set;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Dec-2007<br><br>
 */
public class AxiomListFrame extends AbstractOWLFrame<Set<OWLAxiom>> {


    public AxiomListFrame(OWLEditorKit owlEditorKit) {
        super(owlEditorKit.getModelManager().getOWLOntologyManager());
        addSection(new AxiomListFrameSection(owlEditorKit, this));
    }
}
