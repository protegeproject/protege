package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.ui.action.ActionTarget;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 09-May-2007<br><br>
 */
public interface CreateNewTarget extends ActionTarget {

    boolean canCreateNew();


    void createNewObject();
}
