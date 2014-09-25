package org.protege.editor.owl.model.io;

import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.OWLEditorKit;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 16-Sep-2008<br><br>
 */
public abstract class IOListenerPluginInstance extends IOListener implements ProtegePluginInstance {

    private OWLEditorKit editorKit;

    protected void setup(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }

    protected OWLEditorKit getOWLEditorKit() {
        return editorKit;
    }
}
