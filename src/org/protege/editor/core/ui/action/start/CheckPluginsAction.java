package org.protege.editor.core.ui.action.start;

import java.awt.event.ActionEvent;

import org.protege.editor.core.update.PluginManager;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 6, 2008<br><br>
 */
public class CheckPluginsAction extends AltStartupAction {
    private static final long serialVersionUID = 1531046176436352912L;


    public void actionPerformed(ActionEvent event) {
        PluginManager.getInstance().performCheckPlugins();
    }


    public void initialise() throws Exception {
        // do nothing
    }


    public void dispose() throws Exception {
        // do nothing
    }
}
