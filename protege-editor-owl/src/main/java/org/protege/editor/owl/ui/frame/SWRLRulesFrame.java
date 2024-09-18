package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLOntology;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 06-Jul-2007<br>
 * <br>
 */
public class SWRLRulesFrame extends AbstractOWLFrame<OWLOntology> {

	public SWRLRulesFrame(OWLEditorKit editorKit) {
		super(editorKit.getModelManager().getOWLOntologyManager());
		addSection(new SWRLRulesFrameSection(editorKit, this));
	}

	public SWRLRulesFrame(OWLOntologyManager owlOntologyManager) {
		super(owlOntologyManager);
	}
}
