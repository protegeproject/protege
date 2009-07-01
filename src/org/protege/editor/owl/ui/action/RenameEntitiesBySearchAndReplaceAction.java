package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.refactor.EntityFindAndReplaceURIRenamer;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.rename.RenameEntitiesPanel;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 1, 2008<br><br>
 */
public class RenameEntitiesBySearchAndReplaceAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent event) {
        RenameEntitiesPanel panel = new RenameEntitiesPanel(getOWLEditorKit());
        final UIHelper uiHelper = new UIHelper(getOWLEditorKit());
        if (uiHelper.showValidatingDialog("Change multiple entity URIs", panel, panel.getFocusComponent()) == JOptionPane.OK_OPTION){
            EntityFindAndReplaceURIRenamer renamer = panel.getRenamer();
            List<OWLOntologyChange> changes = renamer.getChanges();
            getOWLModelManager().applyChanges(changes);
        }
    }


    public void initialise() throws Exception {
        // do nothing
    }


    public void dispose() throws Exception {
        // do nothing
    }
}
