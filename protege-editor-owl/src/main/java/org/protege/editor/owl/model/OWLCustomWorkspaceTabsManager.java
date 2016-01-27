package org.protege.editor.owl.model;

import org.protege.editor.core.ui.workspace.CustomWorkspaceTabsManager;
import org.protege.editor.core.ui.workspace.TabbedWorkspace;
import org.protege.editor.core.ui.workspace.WorkspaceTabPlugin;
import org.protege.editor.core.ui.workspace.WorkspaceViewsTab;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;
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
