package org.protege.editor.core.ui.view;

import org.protege.editor.core.ui.action.ToolBarActionPlugin;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 28, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A <code>ViewActionPlugin</code> is a <code>ToolBarActionPlugin</code> that
 * appears on a specific type of <code>View</code>.
 */
public interface ViewActionPlugin extends ToolBarActionPlugin {

    public View getView();
}
