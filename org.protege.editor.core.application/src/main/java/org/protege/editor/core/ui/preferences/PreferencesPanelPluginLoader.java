package org.protege.editor.core.ui.preferences;


import org.eclipse.core.runtime.IExtension;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.plugin.AbstractApplicationPluginLoader;
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
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesPanelPluginLoader extends AbstractApplicationPluginLoader<PreferencesPanelPlugin> {

    private EditorKit editorKit;


    public PreferencesPanelPluginLoader(EditorKit editorKit) {
        super(PreferencesPanelPluginJPFImpl.ID);
        this.editorKit = editorKit;
    }


    protected PluginExtensionMatcher getExtensionMatcher() {
        return new PluginParameterExtensionMatcher();
    }


    protected PreferencesPanelPlugin createInstance(IExtension extension) {
        return new PreferencesPanelPluginJPFImpl(extension, editorKit);
    }
}
