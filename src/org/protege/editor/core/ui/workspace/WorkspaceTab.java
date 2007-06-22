package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.plugin.ProtegePluginInstance;

import javax.swing.*;
import java.net.URL;
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
 * Medical Informatics Group<br>
 * Date: Mar 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * Represents a tab in a <code>TabbedWorkspace</code>.
 * <p/>
 * This is a core plugin type.
 */
public abstract class WorkspaceTab extends JComponent implements ProtegePluginInstance {

    /**
     * The ID of this particular plugin extension.
     */
    private String id;

    private String label;

    private Icon icon;

    private TabbedWorkspace workspace;

    private URL defaultViewConfigurationFile;


    public void setup(WorkspaceTabPlugin plugin) {
        this.id = plugin.getId();
        this.label = plugin.getLabel();
        this.icon = plugin.getIcon();
        this.workspace = plugin.getWorkspace();
        this.defaultViewConfigurationFile = plugin.getDefaultViewConfigFile();
    }


    public String getId() {
        return id;
    }


    public URL getDefaultViewConfigurationFile() {
        return defaultViewConfigurationFile;
    }


    /**
     * Gets this tabs label.  This label will be used in the
     * <code>Workspace</code> UI, for displaying the name of the tab.
     * @return A <code>String</code> representing the label.
     */
    public String getLabel() {
        return label;
    }


    /**
     * Gets this tabs icon.
     * @return An <code>Icon</code> or <code>null</code> if the tab
     *         does not have an icon.
     */
    public Icon getIcon() {
        return icon;
    }


    /**
     * Gets this tab's <code>Workspace</code>, which can be used to
     * access the <code>EditorKit</code>.
     */
    public TabbedWorkspace getWorkspace() {
        return workspace;
    }


    public void requestSelection() {
        workspace.setSelectedTab(this);
    }


    public void save() {
        // Does nothing by default
    }
}
