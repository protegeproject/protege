package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.JOptionPane;

import org.protege.editor.core.update.PluginManager;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 6, 2008<br><br>
 */
public class CheckPluginsAction extends ProtegeAction {



    public void actionPerformed(ActionEvent event) {
        try {
            PluginManager.getInstance().runCheckForPlugins();
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(getWorkspace(),
                    "<html><body>" +
                    "<b>Protege could not connect to the plugin registry.</b><br><br>  " +
                    "Please check your internet connection and try again." +
                    "</body></html>", "Unable to check for plugins", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getWorkspace(),
                    "<html><body>" +
                            "<b>Protege could not connect to the plugin registry.</b><br><br>  " +
                            "Reason: " + e.getMessage() +
                            "</body></html>", "Unable to check for plugins", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void initialise() throws Exception {
        // do nothing
    }


    public void dispose() throws Exception {
        // do nothing
    }
}
