package org.protege.editor.core.ui.action;

import javax.swing.JMenu;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class ProtegeDynamicAction extends ProtegeAction {

    public abstract void rebuildChildMenuItems(JMenu thisMenuItem);
}
