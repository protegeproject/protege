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
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 29, 2006<br><br>

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

    public static final String NAVIGATES = "navigates";

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


    public Set<String> getNavigates() {
        return PluginProperties.getParameterValues(extension, NAVIGATES);
    }


    /**
     * Creates an instance of the plugin.  It is expected that
     * this instance will be "setup", but the instance's
     * initialise method will not have been called in the instantiation
     * process.
     */
    public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        ExtensionInstantiator<ViewComponent> instantiator = new ExtensionInstantiator<>(extension);
        ViewComponent viewComponent = instantiator.instantiate();
        viewComponent.setup(this);
        return viewComponent;
    }
}
