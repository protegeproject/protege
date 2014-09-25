package org.protege.editor.owl.ui.ontology.wizard.move;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.util.Set;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 19-Sep-2008<br><br>
 */
public interface MoveAxiomsModel {

    Set<OWLOntology> getSourceOntologies();

    void setSourceOntologies(Set<OWLOntology> ontologies);

    OWLOntologyID getTargetOntologyID();

    void setTargetOntologyID(OWLOntologyID ontology);

    Set<OWLAxiom> getAxiomsToBeMoved();

//    public void setCopyAxioms(boolean b);

//    public boolean isCopyAxioms();
}
