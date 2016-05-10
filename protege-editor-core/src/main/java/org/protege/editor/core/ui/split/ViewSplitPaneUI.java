package org.protege.editor.core.ui.split;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 19, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ViewSplitPaneUI extends BasicSplitPaneUI {

    /**
     * Creates the default divider.  This is overriden to
     * provide our look & feel ("flat") specific divider.
     */
    public BasicSplitPaneDivider createDefaultDivider() {
        return new ViewSplitPaneDivider(this);
    }


    /**
     * Gets the minimum location of the divider.
     */
    public int getMinimumDividerLocation(JSplitPane jc) {
        return 0;
    }


    /**
     * Gets the maximum location of the divider.
     */
    public int getMaximumDividerLocation(JSplitPane jc) {
        return Integer.MAX_VALUE;
    }
}
