package org.protege.editor.core.ui.view;

import java.net.URL;

import org.protege.editor.core.ui.workspace.WorkspaceTab;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-Mar-2007<br><br>
 */
public class ViewsPaneMemento {

    private URL initialCongigFileURL;

    private String viewPaneId;

    private boolean forceReset;


    public ViewsPaneMemento(URL initialConfigFileURL, String viewPaneId) {
        this.initialCongigFileURL = initialConfigFileURL;
        this.viewPaneId = viewPaneId;
    }


    public ViewsPaneMemento(URL initialCongigFileURL, String viewPaneId, boolean forceReset) {
        this.initialCongigFileURL = initialCongigFileURL;
        this.viewPaneId = viewPaneId;
        this.forceReset = forceReset;
    }


    public ViewsPaneMemento(WorkspaceTab workspaceTab) {
        initialCongigFileURL = workspaceTab.getDefaultViewConfigurationFile();
        viewPaneId = workspaceTab.getId();
    }


    public URL getInitialCongigFileURL() {
        return initialCongigFileURL;
    }


    public String getViewPaneId() {
        return viewPaneId;
    }


    public boolean isForceReset() {
        return forceReset;
    }


    public void setForceReset(boolean forceReset) {
        this.forceReset = forceReset;
    }
}
