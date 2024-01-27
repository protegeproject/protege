package org.protege.editor.owl.ui.ontology.wizard.move;

import java.util.List;
import java.util.Set;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 11-Sep-2008<br><br>
 *
 * Provides a kit to select axioms to be moved from some source ontologies to
 * a target ontology.
 */
public abstract class MoveAxiomsKit implements ProtegePluginInstance {

    private OWLEditorKit owlEditorKit;

    private String name;

    private String id;

    // Package
    void setup(String id, String name, OWLEditorKit editorKit) {
        this.id = id;
        this.name = name;
        this.owlEditorKit = editorKit;
    }

    final public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    protected OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }

    protected OWLModelManager getOWLModelManager() {
        return getOWLEditorKit().getModelManager();
    }

    public abstract List<MoveAxiomsKitConfigurationPanel> getConfigurationPanels();

    public abstract Set<OWLAxiom> getAxioms(Set<OWLOntology> sourceOntologies);


}
