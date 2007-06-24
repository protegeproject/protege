package org.protege.editor.core.ui.view;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.plugin.ExtensionInstantiator;
import org.protege.editor.core.plugin.JPFUtil;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.ui.workspace.Workspace;

import java.awt.*;
import java.util.Set;
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
 * Date: Mar 29, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewComponentPluginJPFImpl implements ViewComponentPlugin {

    public static final String ID = "ViewComponent";

    public static final String LABEL_PARAM = "label";

    public static final String LOCATION_PARAM = "location";

    public static final String WORKSPACE_TAB_ID_PARAM = "workspaceTabId";

    public static final String HEADER_COLOR_PARAM = "headerColor";

    public static final String USER_CREATABLE_PARAM = "userCreatable";

    public static final String CATEGORY = "category";

    private IExtension extension;

    private Workspace workspace;

    private static final Color DEFAULT_COLOR = Color.LIGHT_GRAY;


    public ViewComponentPluginJPFImpl(Workspace workspace, IExtension extension) {
        this.extension = extension;
        this.workspace = workspace;
    }


    public String getId() {
        return extension.getUniqueIdentifier();
    }


    public Workspace getWorkspace() {
        return workspace;
    }


    public String getDocumentation() {
        return JPFUtil.getDocumentation(extension);
    }


    public String getLabel() {
        return PluginProperties.getParameterValue(extension, LABEL_PARAM, "<Error! No label defined>");
    }


    public boolean isUserCreatable() {
        return PluginProperties.getBooleanParameterValue(extension, USER_CREATABLE_PARAM, true);
    }


    public Color getBackgroundColor() {
        String val = PluginProperties.getParameterValue(extension, HEADER_COLOR_PARAM, null);
        if (val != null) {
            return PropertyUtil.getColor(val, DEFAULT_COLOR);
        }
        return DEFAULT_COLOR;
    }


    public Set<String> getCategorisations() {
        return PluginProperties.getParameterValues(extension, CATEGORY);
    }


    /**
     * Creates an instance of the plugin.  It is expected that
     * this instance will be "setup", but the instance's
     * initialise method will not have been called in the instantiation
     * process.
     */
    public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        ExtensionInstantiator<ViewComponent> instantiator = new ExtensionInstantiator<ViewComponent>(extension);
        ViewComponent viewComponent = instantiator.instantiate();
        viewComponent.setup(this);
        return viewComponent;
    }
}
