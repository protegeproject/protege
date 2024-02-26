package org.protege.editor.owl.ui.error;

import java.net.URI;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.JComponent;

import org.protege.editor.core.ui.error.ErrorExplainer;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 11, 2008<br><br>
 */
public class ParseErrorPanel<O extends Throwable> extends ErrorPanel<O>{


	public ParseErrorPanel(final ErrorExplainer.ErrorExplanation<? extends O> oErrorExplanation, URI loc) {
        super(oErrorExplanation, loc);
    }


    protected static void removeComponentFromParent(JComponent component) {
        if (component.getParent() != null){
            component.getParent().remove(component);
        }
    }

}

