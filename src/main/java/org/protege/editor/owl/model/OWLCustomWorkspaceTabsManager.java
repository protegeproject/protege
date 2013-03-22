package org.protege.editor.owl.model;

import org.protege.editor.core.ui.workspace.CustomWorkspaceTabsManager;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;
import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
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
 * Date: Sep 10, 2008<br><br>
 *
 * Overloads the basic P4 custom tab plugin mechanism to allow the base type to be more specialised
 * ie all OWL tabs should implement OWLWorkspaceViewsTab in order for selection etc to work 
 */
public class OWLCustomWorkspaceTabsManager extends CustomWorkspaceTabsManager {

    protected WorkspaceTabPlugin createPlugin(String name, TabbedWorkspace workspace) {
        return new CustomOWLTabPlugin(name, workspace);
    }


    protected class CustomOWLTabPlugin extends CustomWorkspaceTabsManager.CustomTabPlugin{

        public CustomOWLTabPlugin(String name, TabbedWorkspace workspace) {
            super(name, workspace);
        }


        protected WorkspaceViewsTab createCustomTab(String label) {
            return new CustomOWLTab(label);
        }
    }


    private class CustomOWLTab extends OWLWorkspaceViewsTab {

        /**
         * 
         */
        private static final long serialVersionUID = 8673441963480431647L;
        private String label;

        public CustomOWLTab(String label) {
            this.label = label;
            setToolTipText("Drop views from the Views menu to create a custom view layout");
        }


        public String getLabel() {
            return label;
        }


        public String getId() {
            return label;
        }
    }
}
