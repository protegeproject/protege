package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.OntologyVisibilityManager;
import org.protege.editor.owl.model.OntologyVisibilityManagerImpl;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.VisibilityManagerSelectionStrategy;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.selector.OWLOntologySelectorPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;/*
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
 * Date: Jun 6, 2008<br><br>
 */
public class VisibilityManagerOntologySelectionStrategyAction extends AbstractOntologySelectionStrategyAction {

    private OntologySelectionStrategy strategy;

    private OntologyVisibilityManager vm;

    private OWLOntologySelectorPanel ontPanel;


    public void actionPerformed(ActionEvent event) {
        if (ontPanel == null){
            ontPanel = new OWLOntologySelectorPanel(getOWLEditorKit());
        }
        int ret = new UIHelper(getOWLEditorKit()).showDialog("Select ontologies", ontPanel);
        if (ret == JOptionPane.OK_OPTION) {
            getVisibilityManager().setVisible(ontPanel.getSelectedOntologies());
            super.actionPerformed(event);
        }
    }


    protected OntologySelectionStrategy getStrategy() {
        if (strategy == null){
            strategy = new VisibilityManagerSelectionStrategy(getVisibilityManager());
        }
        return strategy;
    }


    public OntologyVisibilityManager getVisibilityManager() {
        if (vm == null){
            vm = new OntologyVisibilityManagerImpl();
        }
        return vm;
    }
}
