package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;
import java.util.Set;
/*
 * Copyright (C) 2008, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
