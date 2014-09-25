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
 * Date: Jul 25, 2008<br><br>
 */
public class AutoIDException extends Exception{

	private static final long serialVersionUID = 7843272693263950056L;

	public AutoIDException(String s) {
        super(s);
    }
    
    public AutoIDException(String message, Throwable t) {
    	super(message, t);
    }
}
