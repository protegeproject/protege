package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLIcons;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.util.OWLEntityRemover;
import org.semanticweb.owl.util.OWLEntitySetProvider;

import java.awt.event.ActionEvent;
/*
 * Copyright (C) 2007, University of Manchester
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Apr-2007<br><br>
 */
public class DeleteIndividualAction extends OWLSelectionViewAction {

    private OWLEditorKit owlEditorKit;

    private OWLEntitySetProvider<OWLIndividual> indSetProvider;


    public DeleteIndividualAction(OWLEditorKit owlEditorKit, OWLEntitySetProvider<OWLIndividual> indSetProvider) {
        super("Delete individual(s)", OWLIcons.getIcon("individual.delete.png"));
        this.owlEditorKit = owlEditorKit;
        this.indSetProvider = indSetProvider;
    }


    public void updateState() {
        setEnabled(!indSetProvider.getEntities().isEmpty());
    }


    public void dispose() {
    }


    public void actionPerformed(ActionEvent e) {
        OWLEntityRemover remover = new OWLEntityRemover(owlEditorKit.getOWLModelManager().getOWLOntologyManager(),
                                                        owlEditorKit.getOWLModelManager().getOntologies());
        for (OWLIndividual ind : indSetProvider.getEntities()) {
            ind.accept(remover);
        }
        owlEditorKit.getOWLModelManager().applyChanges(remover.getChanges());
    }
}
