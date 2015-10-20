package org.protege.editor.owl.ui.error;

import org.slf4j.Logger;
import org.protege.editor.core.ui.error.ErrorExplainer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URI;
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
 * Date: Dec 11, 2008<br><br>
 */
public class ParseErrorPanel<O extends Throwable> extends ErrorPanel<O>{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5370795163855807014L;


	public ParseErrorPanel(final ErrorExplainer.ErrorExplanation<O> oErrorExplanation, URI loc) {
        super(oErrorExplanation, loc);
    }


    protected static void removeComponentFromParent(JComponent component) {
        if (component.getParent() != null){
            component.getParent().remove(component);
        }
    }

}

