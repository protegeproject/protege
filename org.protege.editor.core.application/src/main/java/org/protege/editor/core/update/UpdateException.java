package org.protege.editor.core.update;

import java.net.URL;
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
 * Date: Nov 4, 2008<br><br>
 */
public class UpdateException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = -6812551693275528899L;

    public UpdateException(String id, URL url, String message) {
        super(id + ": problem with update file (" + url + "). " + message);
    }
}
