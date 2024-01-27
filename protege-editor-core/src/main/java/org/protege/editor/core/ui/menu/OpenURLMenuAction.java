package org.protege.editor.core.ui.menu;

import java.awt.event.ActionEvent;
import java.net.URL;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Aug 12, 2008<br><br>
 */
public class OpenURLMenuAction extends ProtegeAction {

        private URL address;


    public OpenURLMenuAction(URL address) {
        this.address = address;
    }


    public void actionPerformed(ActionEvent event) {
        NativeBrowserLauncher.openURL(address.toString());
    }


    public void initialise() throws Exception {
    }


    public void dispose() throws Exception {
    }
}
