package org.protege.editor.core.ui.tree;

import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;
import java.awt.event.MouseEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 13-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ProtegeTreeUI extends BasicTreeUI {

    public ProtegeTreeUI(BasicTreeUI delegate) {
    }


    /**
     * Messaged to update the selection based on a MouseEvent over a
     * particular row. If the event is a toggle selection event, the
     * row is either selected, or deselected. If the event identifies
     * a multi selection event, the selection is updated from the
     * anchor point. Otherwise the row is selected, and if the event
     * specified a toggle event the row is expanded/collapsed.
     */
    protected void selectPathForEvent(TreePath path, MouseEvent event) {

    }
}
