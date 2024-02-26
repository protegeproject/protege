package org.protege.editor.core.ui.preferences.node;

import javax.swing.JComponent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface PreferenceNode<V extends Object> {

    public String getLabel();


    public V getValue();


    public void setValue(V object);


    public JComponent getComponent();
}
