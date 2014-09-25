package org.protege.editor.owl.ui.selector;

import java.util.Set;
/*
 * Copyright (C) 2008, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Information Management Group<br> Date:
 * 21-Sep-2008<br><br>
 */
public interface OWLObjectSelector<O> {

    O getSelectedObject();

    Set<O> getSelectedObjects();
}
