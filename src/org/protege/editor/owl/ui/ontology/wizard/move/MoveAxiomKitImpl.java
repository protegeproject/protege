package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.owl.model.selection.axioms.AxiomSelectionStrategy;
import org.protege.editor.owl.model.selection.axioms.AllAxiomsStrategy;
import org.semanticweb.owl.model.OWLOntology;

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
 */
public class MoveAxiomKitImpl<S extends AxiomSelectionStrategy> extends MoveAxiomsKit<S> {

    private S strategy;

    private StrategyEditorFactory editorFactory;

    public MoveAxiomKitImpl(S strategy) {
        this.strategy = strategy;
    }

    public StrategyEditor<S> getStrategyEditor(Set<OWLOntology> ontologies) {
        return editorFactory.getEditor(strategy, ontologies, true);
    }


    public S getAxiomSelectionStrategy() {
        return strategy;
    }


    public void initialise() throws Exception {
        editorFactory = new StrategyEditorFactory(getOWLEditorKit());
    }


    public void dispose() throws Exception {
    }


    public boolean hasEditor() {
        return !(strategy instanceof AllAxiomsStrategy);
    }
}
