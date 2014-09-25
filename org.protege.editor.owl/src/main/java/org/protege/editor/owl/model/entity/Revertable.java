package org.protege.editor.owl.model.entity;
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
 * Date: Sep 18, 2008<br><br>
 */
public interface Revertable {

    void checkpoint();

    void revert();
}
