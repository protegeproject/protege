package org.protege.editor.core.ui.action;

import javax.swing.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class ProtegeDynamicAction extends ProtegeAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8038803274995974199L;

    public abstract void rebuildChildMenuItems(JMenu thisMenuItem);
}
