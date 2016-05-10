package org.protege.editor.owl.model;

import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.owl.OWLEditorKit;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public abstract class OWLEditorKitHook extends EditorKitHook {

    protected OWLEditorKit getEditorKit() {
        return (OWLEditorKit)super.getEditorKit();
    }
}
