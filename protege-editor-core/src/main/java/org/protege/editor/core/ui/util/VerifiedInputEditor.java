package org.protege.editor.core.ui.util;
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
 * Date: Mar 14, 2008<br><br>
 */
public interface VerifiedInputEditor {

    void addStatusChangedListener(InputVerificationStatusChangedListener listener);

    void removeStatusChangedListener(InputVerificationStatusChangedListener listener);
}
