package org.protege.editor.core.ui.action;

import java.util.Comparator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 31, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ToolBarActionComparator implements Comparator<ToolBarActionPlugin> {

    public int compare(ToolBarActionPlugin o1, ToolBarActionPlugin o2) {
        int result = o1.getGroup().compareTo(o2.getGroup());
        if (result != 0) {
            return result;
        }
        return o1.getGroupIndex().compareTo(o2.getGroupIndex());
    }
}
