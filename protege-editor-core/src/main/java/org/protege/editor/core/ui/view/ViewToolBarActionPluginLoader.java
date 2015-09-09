package org.protege.editor.core.ui.view;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.PluginParameterExtensionMatcher;
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
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewToolBarActionPluginLoader extends AbstractPluginLoader<ViewActionPlugin> {

    private EditorKit editorKit;

    private View view;


    public ViewToolBarActionPluginLoader(EditorKit editorKit, View view) {
        super(ProtegeApplication.ID, ViewActionPluginJPFImpl.EXTENSION_POINT_ID);
        this.editorKit = editorKit;
        this.view = view;
    }


    /**
     * This method needs to be overriden to provide a
     * <code>PluginExtensionMatcher</code>, which is used to filter
     * the plugin extensions to a desired subset.
     */
    protected PluginExtensionMatcher getExtensionMatcher() {
        PluginParameterExtensionMatcher matcher = new PluginParameterExtensionMatcher();
        matcher.put(ViewActionPluginJPFImpl.VIEW_ID_PARAM, view.getId());
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
    protected ViewActionPluginJPFImpl createInstance(IExtension extension) {
        return new ViewActionPluginJPFImpl(editorKit, view, extension);
    }
}
