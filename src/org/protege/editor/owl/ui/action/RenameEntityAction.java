package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.rename.RenameEntityPanel;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.OWLEntityRenamer;

import java.net.URI;
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
 * Date: 21-Feb-2007<br><br>
 */
public class RenameEntityAction extends SelectedOWLEntityAction {


    protected void actionPerformed(OWLEntity selectedEntity) {
        OWLEntityRenamer owlEntityRenamer = new OWLEntityRenamer(getOWLModelManager().getOWLOntologyManager(),
                                                                 getOWLModelManager().getOntologies());
        final URI uri = RenameEntityPanel.showDialog(getOWLEditorKit(), selectedEntity);
        if (uri == null) {
            return;
        }
        getOWLModelManager().applyChanges(owlEntityRenamer.changeURI(selectedEntity.getURI(), uri));
        selectedEntity.accept(new OWLEntityVisitor() {
            public void visit(OWLClass cls) {
                ensureSelected(getOWLDataFactory().getOWLClass(uri));
            }


            public void visit(OWLObjectProperty property) {
                ensureSelected(getOWLDataFactory().getOWLObjectProperty(uri));
            }


            public void visit(OWLDataProperty property) {
                ensureSelected(getOWLDataFactory().getOWLDataProperty(uri));
            }


            public void visit(OWLIndividual individual) {
                ensureSelected(getOWLDataFactory().getOWLIndividual(uri));
            }


            public void visit(OWLDataType dataType) {
            }
        });
    }


    private void ensureSelected(OWLEntity entity) {
        getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(entity);
    }


    protected void disposeAction() throws Exception {
    }
}
