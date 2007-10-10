package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.action.ActionTarget;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-May-2007<br><br>
 */
public interface Deleteable extends ActionTarget {

    boolean canDelete();


    void handleDelete();
}
