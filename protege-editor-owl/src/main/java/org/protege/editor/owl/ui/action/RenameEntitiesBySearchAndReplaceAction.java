package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.rename.RenameEntitiesPanel;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

/*
* Copyright (C) 2007, University of Manchester
*
*
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

	private static final long serialVersionUID = 323702328213956558L;


	public void actionPerformed(ActionEvent event) {
        RenameEntitiesPanel panel = new RenameEntitiesPanel(getOWLEditorKit());
        final UIHelper uiHelper = new UIHelper(getOWLEditorKit());
        if (uiHelper.showValidatingDialog("Change multiple entity URIs", panel, panel.getFocusComponent()) == JOptionPane.OK_OPTION){
            List<OWLOntologyChange> changes = panel.getChanges();
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
