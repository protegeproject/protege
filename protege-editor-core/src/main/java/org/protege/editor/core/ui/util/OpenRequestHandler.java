package org.protege.editor.core.ui.util;

import org.protege.editor.core.ui.workspace.Workspace;
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
 * Date: Sep 22, 2008<br><br>
 */
public interface OpenRequestHandler {

    Workspace getCurrentWorkspace();

    void openInNewWorkspace() throws Exception;

    void openInCurrentWorkspace() throws Exception;
}
