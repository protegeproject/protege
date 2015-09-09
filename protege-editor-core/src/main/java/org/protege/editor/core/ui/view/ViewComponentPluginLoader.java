package org.protege.editor.core.ui.view;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractApplicationPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
import org.protege.editor.core.ui.workspace.Workspace;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 18, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewComponentPluginLoader extends AbstractApplicationPluginLoader<ViewComponentPlugin> {

    private Workspace workspace;

//    private String tabId;


    public ViewComponentPluginLoader(Workspace tab) {
        super(ViewComponentPluginJPFImpl.ID);
        this.workspace = tab;
//        this.tabId = tabId;
    }


    protected PluginExtensionMatcher getExtensionMatcher() {
        PluginParameterExtensionMatcher matcher = new PluginParameterExtensionMatcher();
//        matcher.put(ViewComponentPluginJPFImpl.WORKSPACE_TAB_ID_PARAM, tabId);
        return matcher;
    }


    protected ViewComponentPlugin createInstance(IExtension extension) {
        return new ViewComponentPluginJPFImpl(workspace, extension);
    }
}
