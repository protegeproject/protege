package org.protege.editor.core.ui.workspace;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.plugin.AbstractApplicationPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
import org.protege.editor.core.plugin.PluginProperties;
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

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A utility class that loads <code>WorkspaceTabPlugin</code>s.  The
 * tab plugins are filtered so that <code>WorkspaceTabPlugin</code>s
 * that apply to a specific workspace are loaded.
 */
public class WorkspaceTabPluginLoader extends AbstractApplicationPluginLoader<WorkspaceTabPlugin> {

    private TabbedWorkspace workspace;


    /**
     * Creates a <code>WorkspaceTabPluginLoader</code> that will
     * load <code>WorkspaceTabPlugin</code>s that are applicable
     * to the specified <code>Workspace</code>.
     */
    public WorkspaceTabPluginLoader(TabbedWorkspace workspace) {
        super(WorkspaceTabPluginJPFImpl.ID);
        this.workspace = workspace;
    }


    /**
     * This method needs to be overriden to provide a
     * <code>PluginExtensionMatcher</code>, which is used to filter
     * the plugin extensions to a desired subset.
     */
    protected PluginExtensionMatcher getExtensionMatcher() {
        PluginParameterExtensionMatcher matcher = new PluginParameterExtensionMatcher();
        matcher.put(PluginProperties.EDITOR_KIT_PARAM_NAME, workspace.getEditorKit().getId());
        return matcher;
    }


    /**
     * This method needs to be overriden to create an instance
     * of the desired plugin, based on the plugin <code>Extension</code>
     * @param extension The <code>Extension</code> that describes the
     *                  Java Plugin Framework extension.
     * @return A plugin object (typically some sort of wrapper around
     *         the extension)
     */
    protected WorkspaceTabPlugin createInstance(IExtension extension) {
        return new WorkspaceTabPluginJPFImpl(workspace, extension);
    }
}
