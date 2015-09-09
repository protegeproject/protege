package org.protege.editor.owl.model.io;

import org.protege.editor.owl.ProtegeOWL;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.DefaultPluginExtensionMatcher;
import org.eclipse.core.runtime.IExtension;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 16-Sep-2008<br><br>
 */
public class IOListenerPluginLoader extends AbstractPluginLoader<IOListenerPlugin> {

    private OWLEditorKit editorKit;

    public IOListenerPluginLoader(OWLEditorKit editorKit) {
        super(ProtegeOWL.ID, IOListenerPlugin.ID);
        this.editorKit = editorKit;
    }


    protected IOListenerPlugin createInstance(IExtension extension) {
        return new IOListenerPluginImpl(extension, editorKit);
    }
}
