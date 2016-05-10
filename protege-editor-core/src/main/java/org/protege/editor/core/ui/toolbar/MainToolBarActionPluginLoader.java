package org.protege.editor.core.ui.toolbar;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractApplicationPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
import org.protege.editor.core.plugin.PluginProperties;
import org.protege.editor.core.ui.action.ToolBarActionPluginJPFImpl;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 28, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MainToolBarActionPluginLoader extends AbstractApplicationPluginLoader<ToolBarActionPluginJPFImpl> {

    private EditorKit editorKit;


    public MainToolBarActionPluginLoader(EditorKit editorKit) {
        super(ToolBarActionPluginJPFImpl.EXTENSION_POINT_ID);
        this.editorKit = editorKit;
    }


    /**
     * This method needs to be overriden to provide a
     * <code>PluginExtensionMatcher</code>, which is used to filter
     * the plugin extensions to a desired subset.
     */
    protected PluginExtensionMatcher getExtensionMatcher() {
        PluginParameterExtensionMatcher matcher = new PluginParameterExtensionMatcher();
        String editorKitParamVal = "any";
        if (editorKit != null) {
            editorKitParamVal = editorKit.getId();
        }
        matcher.put(PluginProperties.EDITOR_KIT_PARAM_NAME, editorKitParamVal);
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
    protected ToolBarActionPluginJPFImpl createInstance(IExtension extension) {
        return new ToolBarActionPluginJPFImpl(editorKit, extension);
    }
}
