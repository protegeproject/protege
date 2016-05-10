package org.protege.editor.core.ui.action;

import org.protege.editor.core.update.PluginManager;

import java.awt.event.ActionEvent;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 6, 2008<br><br>
 */
public class CheckPluginsAction extends ProtegeAction {



    public void actionPerformed(ActionEvent event) {
        PluginManager.getInstance().runCheckForPlugins();
    }


    public void initialise() throws Exception {
        // do nothing
    }


    public void dispose() throws Exception {
        // do nothing
    }
}
