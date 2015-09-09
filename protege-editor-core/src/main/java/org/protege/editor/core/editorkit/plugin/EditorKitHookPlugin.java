package org.protege.editor.core.editorkit.plugin;

import org.protege.editor.core.plugin.ProtegePlugin;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 15, 2008<br><br>
 */
public interface EditorKitHookPlugin extends ProtegePlugin<EditorKitHook> {

    public static final String EXTENSION_POINT_ID = "EditorKitHook";
}
