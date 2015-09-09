package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.plugin.AbstractPluginLoader;
import org.protege.editor.core.plugin.PluginExtensionMatcher;
import org.protege.editor.core.plugin.DefaultPluginExtensionMatcher;
import org.protege.editor.owl.ProtegeOWL;
import org.protege.editor.owl.OWLEditorKit;
import org.eclipse.core.runtime.IExtension;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 11-Sep-2008<br><br>
 */
public class MoveAxiomsKitPluginLoader extends AbstractPluginLoader<MoveAxiomsKitPlugin> {

    private OWLEditorKit editorKit;

    public MoveAxiomsKitPluginLoader(OWLEditorKit editorKit) {
        super(ProtegeOWL.ID, MoveAxiomsKitPlugin.ID);
        this.editorKit = editorKit;
    }


    protected MoveAxiomsKitPlugin createInstance(IExtension extension) {
        return new MoveAxiomsKitPluginImpl(editorKit, extension);
    }
}
