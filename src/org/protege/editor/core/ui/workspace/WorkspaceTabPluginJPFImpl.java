package org.protege.editor.core.ui.workspace;




import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.PluginProperties;

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
 * Date: Mar 28, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * An implementation of the <code>WorkspaceTabPlugin</code> that uses
 * the Java Plugin Framework to obtain the details of the plugins
 */
public class WorkspaceTabPluginJPFImpl implements WorkspaceTabPlugin {

    /**
     * The plugin extension point ID
     */
    public static final String ID = "WorkspaceTab";

    /**
     * The plugin extension paramter name that identifies the label for the tab
     */
    private static final String LABEL_PARAM = "label";

    /**
     * The plugin extension parameter name that identifies the icon for the tab.
     */
    private static final String ICON_PARAM = "icon";


    /**
     * The plugin extension parameter name that identifies the icon for the tab.
     */
    private static final String INDEX_PARAM = "index";


    private static final String DEFAULT_VIEW_CONFIG_FILE_NAME_PARAM = "defaultViewConfigFileName";

    private TabbedWorkspace workspace;

    private Extension extension;


    public WorkspaceTabPluginJPFImpl(TabbedWorkspace workspace, Extension extension) {
        this.extension = extension;
        this.workspace = workspace;
    }


    /**
     * Gets the workspace that this plugin creates
     * tabs for.
     * @return A <code>TabbedWorkspace</code> that the tabs instantiated
     *         by this plugin belong to.
     */
    public TabbedWorkspace getWorkspace() {
        return workspace;
    }


    public String getId() {
        return extension.getId();
    }


    public String getLabel() {
        return PluginProperties.getParameterValue(extension, LABEL_PARAM, "<Error! No label defined>");
    }


    public String getIndex() {
        return PluginProperties.getParameterValue(extension, INDEX_PARAM, "Z");
    }


    public Icon getIcon() {
        Extension.Parameter param = extension.getParameter(ICON_PARAM);
        if (param == null) {
            return null;
        }
        return null;
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }


    public WorkspaceTab newInstance() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        ExtensionInstantiator<WorkspaceTab> instantiator = new ExtensionInstantiator<WorkspaceTab>(extension);
        WorkspaceTab tab = instantiator.instantiate();
        tab.setup(this);
        return tab;
    }


    public URL getDefaultViewConfigFile() {
        PluginManager manager = PluginManager.lookup(this);
        PluginDescriptor descriptor = extension.getDeclaringPluginDescriptor();
        ClassLoader loader = manager.getPluginClassLoader(descriptor);
        String resource = PluginProperties.getParameterValue(extension, DEFAULT_VIEW_CONFIG_FILE_NAME_PARAM, null);
        return loader.getResource(resource);
    }
}
