package org.protege.editor.owl.ui.ontology.wizard.move.common;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 23-Sep-2008<br><br>
 */
public interface SignatureSelection {

    void setSignature(Set<OWLEntity> entities);

    Set<OWLEntity> getSignature();

    Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies);

    Set<OWLAxiom> getAxioms(Set<OWLOntology> ontologies, Set<OWLEntity> entities);
}
