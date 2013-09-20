package org.protege.editor.owl.ui.action;

import javax.swing.event.ChangeListener;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-May-2007<br><br>
 */
public interface ActionTarget {

    public void addChangeListener(ChangeListener listener);


    public void removeChangeListener(ChangeListener listener);
}
